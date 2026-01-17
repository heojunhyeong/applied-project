package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerOrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    /**
     * ✅ Page + fetch join은 지뢰라서 제거
     * 대신 EntityGraph로 필요한 연관만 한 번에 로딩
     */
    @EntityGraph(attributePaths = {"order", "product"})
    @Query("""
        select od
        from OrderDetail od
        where od.sellerId = :sellerId
    """)
    Page<OrderDetail> findSellerOrderDetails(
            @Param("sellerId") Long sellerId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"order", "product"})
    @Query("""
        select od
        from OrderDetail od
        where od.sellerId = :sellerId
          and od.detailStatus = :status
    """)
    Page<OrderDetail> findSellerOrderDetailsByStatus(
            @Param("sellerId") Long sellerId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    /**
     * 상세 단건은 fetch join 써도 됨 (Page 아님)
     * orderDelivery도 같이 땡겨오려고 join fetch 유지
     */
    @Query("""
        select od
        from OrderDetail od
        join fetch od.order o
        join fetch od.product p
        left join fetch o.orderDelivery d
        where od.id = :orderDetailId
          and od.sellerId = :sellerId
    """)
    Optional<OrderDetail> findDetailByIdAndSellerId(
            @Param("orderDetailId") Long orderDetailId,
            @Param("sellerId") Long sellerId
    );
}
