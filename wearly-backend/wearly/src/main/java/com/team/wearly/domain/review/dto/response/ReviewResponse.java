package com.team.wearly.domain.review.dto.response;

import com.team.wearly.domain.review.entity.ProductReview;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        String reviewerName, // 닉네임 (User 조회 필요)
        int rating,
        String content,
        LocalDateTime createdDate
) {
    public static ReviewResponse of(ProductReview review, String nickname) {
        return new ReviewResponse(
                review.getId(),
                nickname, // 조회한 닉네임 주입
                review.getRating(),
                review.getContent(),
                review.getCreatedDate()
        );
    }
}