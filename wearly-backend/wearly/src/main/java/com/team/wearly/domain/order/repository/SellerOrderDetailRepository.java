package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface SellerOrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    /**
     * 판매자 주문 상세 목록 조회 (전체)
     * OrderDetail 기준
     */
    @Query("""
        select od
        from OrderDetail od
        join fetch od.order o
        join fetch od.product p
        left join fetch OrderDeliveryDetail odd on odd.orderDetail = od
        where od.sellerId = :sellerId
    """)
    Page<OrderDetail> findSellerOrderDetails(
            @Param("sellerId") Long sellerId,
            Pageable pageable
    );

    /**
     * 판매자 주문 상세 목록 조회 (detailStatus 필터)
     */
    @Query("""
        select od
        from OrderDetail od
        join fetch od.order o
        join fetch od.product p
        left join fetch OrderDeliveryDetail odd on odd.orderDetail = od
        where od.sellerId = :sellerId
          and od.detailStatus = :status
    """)
    Page<OrderDetail> findSellerOrderDetailsByStatus(
            @Param("sellerId") Long sellerId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    /**
     * 판매자 주문 상세 단건 조회 (권한 체크)
     */
    @Query("""
        select od
        from OrderDetail od
        join fetch od.order o
        join fetch od.product p
        left join fetch OrderDeliveryDetail odd on odd.orderDetail = od
        left join fetch o.orderDelivery d
        where od.id = :orderDetailId
          and od.sellerId = :sellerId
    """)
    java.util.Optional<OrderDetail> findDetailByIdAndSellerId(
            @Param("orderDetailId") Long orderDetailId,
            @Param("sellerId") Long sellerId
    );
}
