package com.team.wearly.domain.product.entity;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 판매자 식별자
    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(nullable = false)
    private Long price;

    // 재고는 null 허용이면 OK. (근데 보통 0 기본값 추천)
    @Column(nullable = true)
    private Long stockQuantity;

    @Column(nullable = false, length = 2000)
    private String description;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductCategory productCategory;

    // 기본값 세팅 (Builder로 만들 때 status를 안 넣어도 ON_SALE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ON_SALE;

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

    /**  판매 상태 변경 (이 메서드 하나만 남겨) */
    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    /**  소프트 삭제 = 판매중단 */
    public void softDelete() {
        this.status = ProductStatus.SOLD_OUT;
    }

    /**  다시 판매중 */
    public void restore() {
        this.status = ProductStatus.ON_SALE;
    }
}
