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

        // 토스 서버에 최종 승인 요청
        TossConfirmResponse response = tossPaymentClient.confirmPayment(paymentKey, orderId, amount);

        // 승인 결과를 바탕으로 Payment 엔티티 생성
        Payment payment = Payment.builder()
                .paymentKey(response.getPaymentKey())
                .orderId(response.getOrderId())
                .amount(response.getTotalAmount())
                // 승인 성공 시 DONE
                .status(PaymentStatus.DONE)
                // 문자열을 Enum으로 변환
                .method(PaymentMethod.valueOf(response.getMethod()))
                .build();

        // DB에 결제 내역 저장
        paymentRepository.save(payment);

        if (orderId.startsWith("ORD")) {
            completeOrder(orderId);
        } else if (orderId.startsWith("MEM")) {
            completeMembership(orderId);
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
}