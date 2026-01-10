package com.team.wearly.domain.product.seller.dto.response;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;

import java.time.LocalDateTime;

public record SellerProductResponse(
        Long id,
        Long sellerId,
        String productName,
        Long price,
        Long stockQuantity,
        String description,
        String imageUrl,
        Brand brand,
        ProductCategory productCategory,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
    public static SellerProductResponse from(Product p) {
        return new SellerProductResponse(
                p.getId(),
                p.getSellerId(),
                p.getProductName(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getDescription(),
                p.getImageUrl(),
                p.getBrand(),
                p.getProductCategory(),
                p.getCreatedDate(),
                p.getUpdatedDate()
        );
    }
}
