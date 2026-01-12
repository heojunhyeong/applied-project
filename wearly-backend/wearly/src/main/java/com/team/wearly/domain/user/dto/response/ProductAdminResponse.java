package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponse {
    private Long id;                    // 상품 ID
    private Long sellerId;              // 상품을 등록한 seller의 ID
    private String productName;         // 상품명
    private Long price;                 // 상품 가격
    private ProductStatus status;       // 판매 상태

    // 엔티티 -> DTO 변환 편의 메서드
    public static ProductAdminResponse from(Product product) {
        return ProductAdminResponse.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .status(product.getStatus())
                .build();
    }
}
