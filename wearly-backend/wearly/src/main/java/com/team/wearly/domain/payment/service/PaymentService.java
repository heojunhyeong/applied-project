package com.team.wearly.domain.payment.service;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.payment.dto.response.TossBillingConfirmResponse;
import com.team.wearly.domain.payment.entity.Payment;
import com.team.wearly.domain.payment.entity.enums.PaymentMethod;
import com.team.wearly.domain.payment.entity.enums.PaymentStatus;
import com.team.wearly.domain.payment.infrastructure.toss.TossConfirmResponse;
import com.team.wearly.domain.payment.infrastructure.toss.TossPaymentClient;
import com.team.wearly.domain.payment.repository.PaymentRepository;
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

    @Transactional
    public void confirmPayment(Long userId, String paymentKey, String orderId, Long amount) {

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("주문자 정보가 일치하지 않습니다.");
        }

        if (!order.getTotalPrice().equals(amount)) {
            throw new RuntimeException("결제 금액이 일치하지 않습니다.");
        }

        // 1. 토스 승인 요청
        TossConfirmResponse response = tossPaymentClient.confirmPayment(paymentKey, orderId, amount);

        try {
            // 2. 우리 서버 DB 작업 (결제 저장, 주문 상태 변경 등)
            order.updateStatus(OrderStatus.PAID);
            savePaymentAndCompleteOrder(response);
        } catch (Exception e) {
            // 3. 만약 우리 DB 작업 중 에러 발생 시, 토스 결제 취소 API 호출
            tossPaymentClient.cancelPayment(paymentKey, "서버 내부 오류로 인한 자동 결제 취소");
            throw new RuntimeException("결제 처리 중 서버 오류가 발생하여 자동 취소되었습니다.");
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

        // ` 빌링키 저장
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

            // 5. 결제 성공 내역 저장 (기존 단건 결제 시 사용한 메서드 재활용 가능)
            savePaymentAndCompleteOrder(paymentResponse);

        } catch (Exception e) {
            // 최초 결제 실패 시 멤버십 활성화를 취소하거나 예외 처리
            membership.updateStatus(MembershipStatus.INACTIVE);
            throw new RuntimeException("멤버십 최초 결제에 실패하여 구독이 활성화되지 않았습니다.");
        }
    }

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
            // 필요 시 membership.updateStatus(MembershipStatus.INACTIVE);
        }
    }

    private void completeOrder(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 주문 상태를 PAID(결제완료)로 변경
        order.updateStatus(OrderStatus.PAID);
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

    private void savePaymentAndCompleteOrder(TossConfirmResponse response) {
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
}