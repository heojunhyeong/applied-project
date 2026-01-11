package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    // 장바구니 전체 조회
    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user.id = :userId")
    List<Cart> findAllByUserId(@Param("userId") Long userId);

    // 장바구니 상품 추가
    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user.id = :userId AND c.product.id = :productId")
    Optional<Cart> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    // 장바구니 전체 삭제
    void deleteAllByUserId(Long userId);

    // 장바구니 특정 상품 삭제
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
