package com.team.wearly.domain.product.entity;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = true)
    private Long stockQuantity;

    // TEXT 타입을 사용하여 긴 URL도 안전하게 저장
    @Column(nullable = false, columnDefinition = "TEXT")
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
    @Builder.Default
    private ProductStatus status = ProductStatus.ON_SALE;

    // 등록 시 기본값 ACTIVE 보장
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ProductStatus.ON_SALE;
        }
    }

    public void update(
            String productName,
            Long price,
            Long stockQuantity,
            String description,
            String imageUrl,
            Brand brand,
            ProductCategory productCategory,
            ProductStatus status
    ) {
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.productCategory = productCategory;
        this.status = status;
    }

    public void softDelete() {
        this.status = ProductStatus.SOLD_OUT;
    }
    @Column(nullable = true)
    private Long sellerId;  // 상품을 등록한 seller의 ID

    // 판매 상태 변경 메서드
    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    public void decreaseStock(Long quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("상품 [" + this.productName + "]의 재고가 부족합니다. (잔여: " + this.stockQuantity + ")");
        }
        this.stockQuantity -= quantity;
    }
}
