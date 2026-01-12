package com.team.wearly.domain.review.dto.response;

import com.team.wearly.domain.review.entity.enums.ReviewStatus;

import java.time.LocalDateTime;

public class SellerReviewResponse {

    public record SellerReviewItemResponse(
            Long reviewId,
            Long productId,
            Long reviewerId,
            String orderId,
            int rating,
            String content,
            ReviewStatus status,
            LocalDateTime createdDate
    ) {}

    public record SellerReviewSummaryResponse(
            double avgRating,
            long reviewCount
    ) {}
}
