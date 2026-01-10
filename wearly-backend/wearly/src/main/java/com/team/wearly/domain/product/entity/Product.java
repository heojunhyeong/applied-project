package com.team.wearly.domain.product.entity;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
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

    //추가
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

    @Column(nullable = true)
    private Long sellerId;  // 상품을 등록한 seller의 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ON_SALE;

    // 판매 상태 변경 메서드
    public void updateStatus(ProductStatus status) {
        this.status = status;
    }
}
