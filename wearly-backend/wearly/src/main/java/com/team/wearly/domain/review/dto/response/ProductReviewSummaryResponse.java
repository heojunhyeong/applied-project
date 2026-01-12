package com.team.wearly.domain.review.dto.response;

public record ProductReviewSummaryResponse(
        Long productId,
        double avgRating,
        long reviewCount
) {}
