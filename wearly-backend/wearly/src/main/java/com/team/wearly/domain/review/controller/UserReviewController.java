package com.team.wearly.domain.review.controller;

import com.team.wearly.domain.review.dto.request.UserReviewRequest;
import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.repository.ProductReviewRepository;
import com.team.wearly.domain.review.service.UserReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/reviews")
@RequiredArgsConstructor
public class UserReviewController {

    private final UserReviewService userReviewService;
    private final ProductReviewRepository productReviewRepository;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<String> createReview(
            Authentication authentication,
            @RequestBody UserReviewRequest.Create request) {

        // 1L은 테스트용
        Long userId = (authentication != null) ? Long.parseLong(authentication.getName()) : 1L;

        userReviewService.createReview(userId, request);
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId) {

        Long userId = (authentication != null) ? Long.parseLong(authentication.getName()) : 1L;

        userReviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReview>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(userReviewService.getReviewsByProduct(productId));
    }
}