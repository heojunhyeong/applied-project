package com.team.wearly.domain.order.repository;

import com.team.wearly.domain.order.entity.Cart;
import com.team.wearly.domain.product.entity.enums.Size;
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
    // fix : 상품 추가 시 중복 확인: (User + Product + Size) 조합으로 수정
    @Query("SELECT c FROM Cart c JOIN FETCH c.product " +
            "WHERE c.user.id = :userId AND c.product.id = :productId AND c.size = :size")
    Optional<Cart> findByUserIdAndProductIdAndSize(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("size") Size size
    );

    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.id IN :cartIds")
    List<Cart> findAllByIdWithProduct(@Param("cartIds") List<Long> cartIds);

    // 장바구니 전체 삭제
    void deleteAllByUserId(Long userId);

    // 장바구니 특정 상품 삭제
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
