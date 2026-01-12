package com.team.wearly.domain.payment.service;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.payment.entity.Payment;
import com.team.wearly.domain.payment.entity.enums.PaymentMethod;
import com.team.wearly.domain.payment.entity.enums.PaymentStatus;
import com.team.wearly.domain.payment.infrastructure.toss.TossConfirmResponse;
import com.team.wearly.domain.payment.infrastructure.toss.TossPaymentClient;
import com.team.wearly.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public void confirmPayment(String paymentKey, String orderId, Long amount) {
        // 1. 토스 승인 요청
        TossConfirmResponse response = tossPaymentClient.confirmPayment(paymentKey, orderId, amount);

        try {
            // 2. 우리 서버 DB 작업 (결제 저장, 주문 상태 변경 등)
            savePaymentAndCompleteOrder(response);
        } catch (Exception e) {
            // 3. 만약 우리 DB 작업 중 에러 발생 시, 토스 결제 취소 API 호출
            tossPaymentClient.cancelPayment(paymentKey, "서버 내부 오류로 인한 자동 결제 취소");
            throw new RuntimeException("결제 처리 중 서버 오류가 발생하여 자동 취소되었습니다.");
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