package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerOrderRepository extends JpaRepository<Order, Long> {


     // 판매자 주문 목록 (sellerId는 OrderDetail에 존재)
    @Query(
            value = """
                select distinct o
                from Order o
                join o.orderDetails od
                where od.sellerId = :sellerId
            """,
            countQuery = """
                select count(distinct o.id)
                from Order o
                join o.orderDetails od
                where od.sellerId = :sellerId
            """
    )
    Page<Order> findBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);


     // 판매자 주문 목록 + 상태 필터 (상태는 OrderDetail.detailStatus 기준)
    @Query(
            value = """
                select distinct o
                from Order o
                join o.orderDetails od
                where od.sellerId = :sellerId
                  and od.detailStatus = :status
            """,
            countQuery = """
                select count(distinct o.id)
                from Order o
                join o.orderDetails od
                where od.sellerId = :sellerId
                  and od.detailStatus = :status
            """
    )
    Page<Order> findBySellerIdAndDetailStatus(
            @Param("sellerId") Long sellerId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );


     // 판매자 주문 상세 (orderId 기준) - 주문이 해당 seller의 detail을 가지고 있는지 검증 포함
    @Query("""
        select distinct o
        from Order o
        join o.orderDetails od
        left join fetch o.orderDetails fod
        left join fetch fod.product p
        left join fetch o.orderDelivery d
        where o.orderId = :orderId
          and od.sellerId = :sellerId
    """)
    Optional<Order> findDetailByOrderIdAndSellerId(
            @Param("orderId") String orderId,
            @Param("sellerId") Long sellerId
    );

    Optional<Order> findByOrderId(String orderId);
}
