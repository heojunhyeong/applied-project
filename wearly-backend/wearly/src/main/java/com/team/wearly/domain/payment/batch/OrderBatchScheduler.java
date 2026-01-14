package com.team.wearly.domain.payment.batch;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.payment.infrastructure.toss.TossConfirmResponse;
import com.team.wearly.domain.payment.infrastructure.toss.TossPaymentClient;
import com.team.wearly.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderBatchScheduler {

    private final OrderRepository orderRepository;
    private final TossPaymentClient tossPaymentClient; // 토스에 물어보기 위해 주입
    private final PaymentService paymentService;

    @Scheduled(fixedDelay = 1800000) // 30분마다 실행
    @Transactional
    public void syncOrderAndPayment() {
        log.info("결제 상태 동기화 배치 시작...");

        LocalDateTime limit = LocalDateTime.now().minusMinutes(30);
        // 결제 대기 중인 주문들 조회
        List<Order> pendingOrders = orderRepository.findAllByOrderStatusAndCreatedDateBefore(
                OrderStatus.BEFORE_PAID, limit);

        for (Order order : pendingOrders) {
            try {
                TossConfirmResponse status = tossPaymentClient.getPaymentByOrderId(order.getOrderId());

                if (status != null && "DONE".equals(status.getStatus())) {
                    paymentService.savePaymentAndCompleteOrder(status);
                    log.info("알림 지연 주문 복구 완료: {}", order.getOrderId());
                } else {
                    order.updateStatus(OrderStatus.CANCELLED);
                    log.info("미결제 주문 자동 취소: {}", order.getOrderId());
                }
            } catch (Exception e) {
                log.warn("결제 조회 실패 또는 내역 없음 - 주문 취소 처리: {}", order.getOrderId());
                order.updateStatus(OrderStatus.CANCELLED);
            }
        }
    }
}