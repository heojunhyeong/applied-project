package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponse {
    private Long id;                    // 상품 ID
    private Long sellerId;              // 상품을 등록한 seller의 ID
    private String sellerName;          // 상품을 등록한 seller의 로그인 ID
    private String productName;         // 상품명
    private Long price;                 // 상품 가격
    private ProductStatus status;       // 판매 상태
    private Long stockQuantity;         // 재고 수량
    private ProductCategory productCategory; // 카테고리
    private String imageUrl;            // 상품 이미지 URL
    private LocalDateTime createdDate;  // 생성일
    private LocalDateTime updatedDate;  // 수정일

    // 엔티티 -> DTO 변환 편의 메서드 (목록 조회용 - 기존 호환성 유지)
    public static ProductAdminResponse from(Product product) {
        return ProductAdminResponse.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .status(product.getStatus())
                .stockQuantity(product.getStockQuantity())
                .productCategory(product.getProductCategory())
                .imageUrl(product.getImageUrl())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
                .build();
    }
}
