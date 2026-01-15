package com.team.wearly.domain.payment.service;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.payment.dto.response.TossBillingConfirmResponse;
import com.team.wearly.domain.payment.entity.Payment;
import com.team.wearly.domain.payment.entity.enums.PaymentMethod;
import com.team.wearly.domain.payment.entity.enums.PaymentStatus;
import com.team.wearly.domain.payment.infrastructure.toss.TossConfirmResponse;
import com.team.wearly.domain.payment.infrastructure.toss.TossPaymentClient;
import com.team.wearly.domain.payment.repository.PaymentRepository;
import com.team.wearly.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final MembershipRepository membershipRepository;
    private final SettlementService settlementService;

    @Transactional
    public void confirmPayment(String paymentKey, String orderId, Long amount) {

        Order idempotence = orderRepository.findByOrderId(orderId).orElseThrow();
        if (idempotence.getOrderStatus() == OrderStatus.PAID) {
            log.warn("이미 처리된 주문입니다. orderId: {}", orderId);
            return;
        }

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        long expectedAmount = order.getTotalPrice() - order.getCouponDiscountPrice();
        if (expectedAmount != amount) {
            throw new IllegalArgumentException("결제 금액 정합성 오류");
        }

        try {
            TossConfirmResponse response = tossPaymentClient.confirmPayment(paymentKey, orderId, amount);

            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();
                product.decreaseStock(detail.getQuantity());
            }

            savePaymentAndCompleteOrder(response);

        } catch (Exception e) {
            log.error("결제 처리 중 에러 발생 - 보상 트랜잭션 실행: {}", e.getMessage());
            tossPaymentClient.cancelPayment(paymentKey, "시스템 오류: " + e.getMessage());
            throw new RuntimeException("결제 처리에 실패하여 자동 취소되었습니다.", e);
        }
    }

    @Transactional
    public void confirmBilling(Long userId, String authKey, String customerKey) {

        // 빌링키 발급
        TossBillingConfirmResponse billingResponse = tossPaymentClient.issueBillingKey(authKey, customerKey);
        String billingKey = billingResponse.getBillingKey();

        // 멤버십 신청 정보 확인
        Membership membership = membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("멤버십 가입 신청 내역이 없습니다."));

        // 빌링키 저장
        membership.registerBillingInfo(billingKey);

        // 첫 달 요금 즉시 결제 실행
        // 멤버십용 주문번호 생성 (예: MEM-20260113-UUID)
        String membershipOrderId = "MEM-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8);
        Long amount = 4900L; // 멤버십 가격 (실제로는 정책에 따라 가져오기)

        try {
            TossConfirmResponse paymentResponse = tossPaymentClient.executeBillingPayment(
                    billingKey,
                    customerKey,
                    membershipOrderId,
                    amount,
                    "웨어리 프리미엄 멤버십 첫 달 결제"
            );

            // 결제 성공 내역 저장 (기존 단건 결제 시 사용한 메서드 재활용 가능)
            savePaymentAndCompleteOrder(paymentResponse);

        } catch (Exception e) {
            // 최초 결제 실패 시 멤버십 활성화를 취소하거나 예외 처리
            membership.updateStatus(MembershipStatus.EXPIRED);
            throw new RuntimeException("멤버십 최초 결제에 실패하여 구독이 활성화되지 않았습니다.");
        }
    }

//    @Transactional
//    public void confirmBilling(Long userId, String authKey, String customerKey) {
//
//        // 1. 빌링키 발급 (가짜 키로 대체)
//        // TossBillingConfirmResponse billingResponse = tossPaymentClient.issueBillingKey(authKey, customerKey);
//        // String billingKey = billingResponse.getBillingKey();
//        String billingKey = "test_billing_key_12345";
//
//        // 2. 멤버십 신청 정보 확인 (위 SQL로 넣은 데이터가 조회됨)
//        Membership membership = membershipRepository.findByUserId(userId)
//                .orElseThrow(() -> new IllegalArgumentException("멤버십 가입 신청 내역이 없습니다."));
//
//        // 3. 빌링키 저장 및 상태 변경 (ACTIVE로 변하고 결제일 세팅됨)
//        membership.registerBillingInfo(billingKey);
//
//        // 4. 첫 달 요금 즉시 결제 실행 (테스트를 위해 주석 처리)
//    /*
//    try {
//        // 실제 결제 API 호출 생략
//        // savePaymentAndCompleteOrder(paymentResponse);
//    } catch (Exception e) {
//        membership.updateStatus(MembershipStatus.EXPIRED);
//        throw new RuntimeException("멤버십 최초 결제에 실패...");
//    }
//    */
//
//        System.out.println("테스트: 멤버십 활성화 성공! 유저 ID: " + userId);
//    }

    @Transactional
    public void executeScheduledPayment(Membership membership) {
        String orderId = "MEM-AUTO-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8);
        Long amount = 4900L; // 멤버십 고정 가격
        String customerKey = "USER_" + membership.getUserId();

        try {
            // 1. 토스 빌링 결제 API 호출
            TossConfirmResponse response = tossPaymentClient.executeBillingPayment(
                    membership.getBillingKey(),
                    customerKey,
                    orderId,
                    amount,
                    "웨어리 프리미엄 정기 결제"
            );

            // 2. 결제 기록 저장 및 멤버십 다음 결제일 갱신 (한 달 뒤로)
            savePaymentAndCompleteOrder(response);
            membership.registerBillingInfo(membership.getBillingKey()); // 기존 메서드 재활용 (날짜 갱신됨)

        } catch (Exception e) {
            // 결제 실패 시 처리 (예: 상태를 PAYMENT_FAILED로 바꾸고 사용자에게 알림 발송 등)
            log.error("정기 결제 실패 - 유저ID: {}, 사유: {}", membership.getUserId(), e.getMessage());
        }
    }

    private void completeOrder(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 주문 상태를 PAID(결제완료)로 변경
        order.updateStatus(OrderStatus.PAID);
        settlementService.createSettlementData(order);

    }

    private void completeMembership(String orderId) {
        Membership membership = membershipRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버십 주문입니다."));

        // 멤버십 상태를 ACTIVE(활성)로 변경
        membership.updateStatus(MembershipStatus.ACTIVE);

    }


    @Transactional
    public void cancelPayment(String orderId, String cancelReason) {

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new RuntimeException("취소할 수 없는 주문 상태입니다.");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다."));

        tossPaymentClient.cancelPayment(payment.getPaymentKey(), cancelReason);

        order.cancel();
        payment.markAsCancelled();
    }

    private PaymentMethod convertToEnum(String method) {
        if (method == null) return null;
        return switch (method) {
            case "카드", "CARD" -> PaymentMethod.CARD;
            case "계좌이체", "TRANSFER" -> PaymentMethod.TRANSFER;
            default -> PaymentMethod.CARD; // 기본값
        };
    }

    public void savePaymentAndCompleteOrder(TossConfirmResponse response) {
        Payment payment = Payment.builder()
                .paymentKey(response.getPaymentKey())
                .orderId(response.getOrderId())
                .amount(response.getTotalAmount())
                .status(PaymentStatus.DONE)
                .method(convertToEnum(response.getMethod()))
                .build();
        paymentRepository.save(payment);

        if (response.getOrderId().startsWith("ORD")) {
            completeOrder(response.getOrderId());
        } else if (response.getOrderId().startsWith("MEM")) {
            completeMembership(response.getOrderId());
        }
    }

    // 추가부분


}