package com.team.wearly.domain.product.entity;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 판매자 식별자 (JWT 붙이면 토큰에서 꺼낸 sellerId로 채우게 됨)
    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = true)
    private Long stockQuantity;

    @Column(nullable = false)
    private String description;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    // 등록 시 기본값 ACTIVE 보장
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ProductStatus.ACTIVE;
        }
    }

    public void update(
            String productName,
            Long price,
            Long stockQuantity,
            String description,
            String imageUrl,
            Brand brand,
            ProductCategory productCategory
    ) {
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.productCategory = productCategory;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    public void softDelete() {
        this.status = ProductStatus.DELETED;
    }
}
