package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findAllByUserIdOrderByCreatedDateDesc(Long userId);
}

