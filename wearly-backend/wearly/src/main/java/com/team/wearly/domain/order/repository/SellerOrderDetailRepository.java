package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerOrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrder_OrderIdAndSellerId(String orderId, Long sellerId);

    List<OrderDetail> findByOrder_IdAndSellerId(Long orderPkId, Long sellerId);

    List<OrderDetail> findBySellerIdAndDetailStatus(Long sellerId, OrderStatus detailStatus);

    Optional<OrderDetail> findByIdAndSellerId(Long id, Long sellerId);
}
