package com.team.wearly.domain.product.repository;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface SellerProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findBySellerIdAndStatusIn(Long sellerId, Collection<ProductStatus> statuses, Pageable pageable);

    Optional<Product> findByIdAndSellerIdAndStatusIn(Long id, Long sellerId, Collection<ProductStatus> statuses);
}
