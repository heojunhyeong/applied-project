package com.team.wearly.domain.user.dto.response;

public record ProfileImagePresignedUrlResponse(
        String presignedUrl,
        String key
) {
}
