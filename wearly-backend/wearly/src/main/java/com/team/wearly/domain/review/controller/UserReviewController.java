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


    /**
     * 사용자가 구매한 상품에 대해 별점과 텍스트 리뷰를 등록함
     *
     * @param authentication 인증된 사용자의 정보
     * @param request 상품 ID, 별점, 리뷰 내용 등을 담은 DTO
     * @return 리뷰 등록 성공 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @PostMapping
    public ResponseEntity<String> createReview(
            Authentication authentication,
            @RequestBody UserReviewRequest.Create request) {

        // 1L은 테스트용
        Long userId = (authentication != null) ? Long.parseLong(authentication.getName()) : 1L;

        userReviewService.createReview(userId, request);
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
    }


    /**
     * 본인이 작성한 리뷰를 삭제하며, 서비스 레이어에서 작성자 일치 여부를 검증함
     *
     * @param authentication 인증된 사용자의 정보
     * @param reviewId 삭제할 리뷰의 식별자
     * @return 리뷰 삭제 완료 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId) {

        Long userId = (authentication != null) ? Long.parseLong(authentication.getName()) : 1L;

        userReviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    /**
     * 특정 상품에 달린 모든 리뷰 목록을 조회함
     *
     * @param productId 조회하고자 하는 상품의 식별자
     * @return 해당 상품의 리뷰 엔티티 리스트
     * @author 허준형
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReview>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(userReviewService.getReviewsByProduct(productId));
    }
}