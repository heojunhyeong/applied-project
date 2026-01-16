package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findAllByUserIdOrderByCreatedDateDesc(Long userId);
    List<Order> findAllByOrderStatusAndCreatedDateBefore(OrderStatus orderStatus, LocalDateTime createdAt);
    // 전체 주문 조회 (최신순)
    List<Order> findAllByOrderByCreatedDateDesc();

    // User 닉네임으로 주문 검색
    @Query("SELECT o FROM Order o JOIN User u ON o.userId = u.id WHERE u.userNickname LIKE %:nickname% ORDER BY o.createdDate DESC")
    List<Order> findByUserNickname(@Param("nickname") String nickname);



    /**
     * 특정 키워드가 포홤된 상품명이 주문된 날짜를 찾고, 그 날짜에 주문된 모든 주문 상세를 반환
     * @return 키워드가 포함된 상품과 같은 날 주문된 모든 OrderDetail 목록
     */
    @Query("""
        SELECT DISTINCT od
        FROM OrderDetail od
        JOIN FETCH od.order o
        JOIN FETCH od.product p
        WHERE DATE(o.createdDate) IN (
            SELECT DISTINCT DATE(o2.createdDate)
            FROM OrderDetail od2
            JOIN od2.order o2
            JOIN od2.product p2
            WHERE p2.productName LIKE %:keyword%
              AND (:userId IS NULL OR o2.userId = :userId)
        )
        AND (:userId IS NULL OR o.userId = :userId)
        ORDER BY o.createdDate DESC, od.id ASC
    """)
    List<OrderDetail> findOrderDetailsByKeywordWithSameDateOrders(
            @Param("keyword") String keyword,
            @Param("userId") Long userId
    );
}

