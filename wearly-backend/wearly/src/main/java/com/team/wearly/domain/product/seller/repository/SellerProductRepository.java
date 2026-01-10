package com.team.wearly.domain.product.seller.repository;

import com.team.wearly.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerProductRepository extends JpaRepository<Product, Long> {

    //한명의 판매자가 판매하는 모든 상품 조회
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    //특정 상품 조회
    Optional<Product> findByIdAndSellerId(Long id, Long sellerId);
}
