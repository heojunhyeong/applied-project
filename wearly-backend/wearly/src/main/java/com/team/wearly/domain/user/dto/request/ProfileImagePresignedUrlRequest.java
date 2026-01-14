package com.team.wearly.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfileImagePresignedUrlRequest(
        @NotBlank(message = "Content-Type은 필수입니다.")
        @Pattern(
                regexp = "^(image/(jpeg|jpg|png|gif|webp))$",
                message = "지원하는 이미지 형식만 업로드 가능합니다. (jpeg, jpg, png, gif, webp)"
        )
        String contentType
) {
}