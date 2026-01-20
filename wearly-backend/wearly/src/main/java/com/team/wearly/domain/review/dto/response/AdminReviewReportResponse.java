package com.team.wearly.domain.review.dto.response;

import com.team.wearly.domain.review.entity.enums.ReviewReportReason;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;
import com.team.wearly.domain.review.entity.enums.ReviewStatus;

import java.time.LocalDateTime;

public record AdminReviewReportResponse(
        Long reportId,
        Long reviewId,
        Long productId,
        Long reviewerId,
        String reviewerName,
        Long reporterId,
        String reporterName,
        String reviewContent,
        int reviewRating,
        ReviewStatus reviewStatus,
        ReviewReportReason reason,
        ReviewReportStatus status,
        LocalDateTime reportCreatedDate,
        LocalDateTime reviewCreatedDate
) {}
