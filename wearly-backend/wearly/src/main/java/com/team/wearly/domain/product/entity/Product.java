package com.team.wearly.domain.product.entity;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import com.team.wearly.domain.product.entity.enums.Size;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    // TEXT 타입을 사용하여 긴 URL도 안전하게 저장 // 상세 이미지 URL
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

    // 상품을 등록한 seller의 ID
    @Column(nullable = true)
    private Long sellerId;

    // 가능한 사이즈 목록 저장 (별도 테이블 자동 생성됨)
    @ElementCollection(fetch = FetchType.EAGER) // // 사이즈 응답에서 안 보이는 문제 방지
    @CollectionTable(
            name = "product_available_sizes", // // 사이즈 저장 테이블
            joinColumns = @JoinColumn(name = "product_id") // // FK
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false) // // size 컬럼
    @Builder.Default
    private Set<Size> availableSizes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ON_SALE;

    // 등록 시 기본값 ON_SALE 보장 // 상태 기본값 세팅 메소드
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ProductStatus.ON_SALE;
        }
        if (this.availableSizes == null) {
            this.availableSizes = new HashSet<>();
        }
    }

    // 상품 전체 정보 업데이트 메소드
    public void update(
            String productName,
            Long price,
            Long stockQuantity,
            String description,
            String imageUrl,
            Brand brand,
            ProductCategory productCategory,
            ProductStatus status,
            Set<Size> availableSizes
    ) {
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.productCategory = productCategory;
        this.status = status;

        // // 사이즈 목록 업데이트 메소드
        this.availableSizes.clear();
        if (availableSizes != null) {
            this.availableSizes.addAll(availableSizes);
        }
    }

    // 상품 논리 삭제(soft delete) 메소드
    public void softDelete() {
        this.status = ProductStatus.DELETED;
    }

    // 판매 상태 변경 메소드
    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    // 재고 감소 메소드
    public void decreaseStock(Long quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("상품 [" + this.productName + "]의 재고가 부족합니다. (잔여: " + this.stockQuantity + ")");
        }
        this.stockQuantity -= quantity;
    }
}
