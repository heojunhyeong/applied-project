package com.team.wearly.domain.review.dto.request;

import lombok.Getter;

@Getter
public class UserReviewRequest {

    // 리뷰 생성을 위한 데이터 묶음
    public record Create(
            Long productId,
            String orderId,
            int rating,
            String content
    ) {}

    // 리뷰 수정을 위한 데이터 묶음
    public record Update(
            String content,
            int rating
    ) {}
}