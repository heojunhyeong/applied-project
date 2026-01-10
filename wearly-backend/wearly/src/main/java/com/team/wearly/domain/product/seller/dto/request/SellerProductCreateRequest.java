package com.team.wearly.domain.product.seller.dto.request;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SellerProductCreateRequest(
        @NotBlank String productName,
        @NotNull Long price,
        Long stockQuantity,
        @NotBlank String description,
        @NotBlank String imageUrl,
        @NotNull Brand brand,
        @NotNull ProductCategory productCategory
) {}
