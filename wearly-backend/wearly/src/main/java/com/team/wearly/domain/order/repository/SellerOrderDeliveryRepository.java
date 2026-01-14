package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerOrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {
    //오더에 따른 배달 정보 조회
    Optional<OrderDelivery> findByOrder(Order order);
}
