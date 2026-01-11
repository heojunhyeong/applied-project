package com.team.wearly.domain.product.dto.request;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SellerProductUpsertRequest(
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 100, message = "상품명은 100자 이내여야 합니다.")
        String productName,

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Long price,

        @NotNull(message = "재고는 필수입니다.")
        @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
        Long stockQuantity,

        @NotBlank(message = "설명은 필수입니다.")
        @Size(max = 2000, message = "설명은 2000자 이내여야 합니다.")
        String description,

        @NotBlank(message = "이미지 URL은 필수입니다.")
        @Size(max = 1000, message = "이미지 URL은 1000자 이내여야 합니다.")
        String imageUrl,

        @NotNull(message = "브랜드는 필수입니다.")
        Brand brand,

        @NotNull(message = "카테고리는 필수입니다.")
        ProductCategory productCategory,

        @NotNull(message = "상태 선택은 필수입니다.")
        ProductStatus status
) {}
