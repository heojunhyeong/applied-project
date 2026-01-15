package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {

    // 주문에 연결된 배송지 조회
    Optional<OrderDelivery> findByOrderId(Long orderId);
}
