package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerOrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    // order에 따른 Item들 조회
    List<OrderDetail> findByOrder(Order order);
}
