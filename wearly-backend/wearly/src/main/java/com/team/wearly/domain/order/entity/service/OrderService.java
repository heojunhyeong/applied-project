package com.team.wearly.domain.order.entity.service;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;


    /**
     * 주문을 생성하고 저장하는 메서드
     * 주문번호는 현재 날짜와 UUID를 조합하며 생성되며
     * 최초 주문 상태는 {@link OrderStatus#BEFORE_PAID} 로 설정된다
     *
     * @param request 주문 생성을 위한 요청 객체
     *                - userId: 주문을 생성하는 사용자 ID
     *                - totalPrice: 주문 총 금액
     * @return 생성되어 저장된 Order 엔티티
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2025-01-11
     */
    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        // 주문번호 생성 (예: ORD-20240110-ab12cd)
        // 날짜와 UUID로 생성
        String orderId = "ORD-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8);

        Order order = Order.builder()
                .orderId(orderId)
                .userId(request.getUserId())
                .totalPrice(request.getTotalPrice())
                .orderStatus(OrderStatus.BEFORE_PAID)
                .build();

        return orderRepository.save(order);
    }
}