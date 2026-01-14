package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findAllByUserIdOrderByCreatedDateDesc(Long userId);
    List<Order> findAllByOrderStatusAndCreatedDateBefore(OrderStatus orderStatus, LocalDateTime createdAt);
}

