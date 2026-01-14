package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findAllByUserIdOrderByCreatedDateDesc(Long userId);


    // 전체 주문 조회 (최신순)
    List<Order> findAllByOrderByCreatedDateDesc();

    // User 닉네임으로 주문 검색
    @Query("SELECT o FROM Order o JOIN User u ON o.userId = u.id WHERE u.userNickname LIKE %:nickname% ORDER BY o.createdDate DESC")
    List<Order> findByUserNickname(@Param("nickname") String nickname);
}

