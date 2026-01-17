package com.team.wearly.domain.seller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SellerProfileImageUpdateRequest(
        String imageUrl
) {
}
