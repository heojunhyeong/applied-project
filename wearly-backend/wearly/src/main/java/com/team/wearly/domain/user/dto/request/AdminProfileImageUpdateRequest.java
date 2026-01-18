package com.team.wearly.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminProfileImageUpdateRequest(
        String imageUrl
) {
}
