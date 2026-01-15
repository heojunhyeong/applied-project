package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerOrderRepository extends JpaRepository<Order, Long> {

    // 판매자 주문 목록 (최신순)
    Page<Order> findBySellerId(Long sellerId, Pageable pageable);

    // 판매자 주문 목록 + 상태 필터
    Page<Order> findBySellerIdAndOrderStatus(Long sellerId, OrderStatus orderStatus, Pageable pageable);

    // 판매자 주문 상세
    Optional<Order> findByOrderIdAndSellerId(String orderId, Long sellerId);

    // orderDetails, product, delivery 같이 가져옴
    @Query("""
        select distinct o
        from Order o
        left join fetch o.orderDetails od
        left join fetch od.product p
        left join fetch o.orderDelivery d
        where o.orderId = :orderId
          and o.sellerId = :sellerId
    """)
    Optional<Order> findDetailByOrderIdAndSellerId(
            @Param("orderId") String orderId,
            @Param("sellerId") Long sellerId
    );
}
