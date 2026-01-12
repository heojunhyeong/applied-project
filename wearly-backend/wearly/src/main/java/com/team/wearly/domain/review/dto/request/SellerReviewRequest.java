package com.team.wearly.domain.review.dto.request;

import com.team.wearly.domain.review.entity.enums.ReviewReportReason;

public class SellerReviewRequest {

    public record ReviewReportCreateRequest(
            ReviewReportReason reason
    ) {}
}
