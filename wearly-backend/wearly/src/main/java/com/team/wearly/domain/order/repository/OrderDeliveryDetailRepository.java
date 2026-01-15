package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.OrderDeliveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDeliveryDetailRepository extends JpaRepository<OrderDeliveryDetail, Long> {

    // 주문상세에 연결된 송장/택배사 조회
    Optional<OrderDeliveryDetail> findByOrderDetailId(Long orderDetailId);
}
