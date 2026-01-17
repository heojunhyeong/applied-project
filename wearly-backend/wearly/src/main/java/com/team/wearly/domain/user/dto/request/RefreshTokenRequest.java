package com.team.wearly.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh Token은 필수입니다")
        String refreshToken
) {}
