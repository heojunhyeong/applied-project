package com.team.wearly.domain.review.dto.response;

import com.team.wearly.domain.review.entity.enums.ReviewReportReason;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;

import java.time.LocalDateTime;

public record SellerReviewReportItemResponse(
        Long reportId,
        Long reviewId,
        Long productId,
        Long reporterId,
        ReviewReportReason reason,
        ReviewReportStatus status,
        LocalDateTime createdDate
) {}
