package com.team.wearly.domain.review.controller;

import com.team.wearly.domain.review.dto.request.SellerReviewRequest;
import com.team.wearly.domain.review.dto.response.ProductReviewSummaryResponse;
import com.team.wearly.domain.review.dto.response.SellerReviewReportItemResponse;
import com.team.wearly.domain.review.dto.response.SellerReviewResponse;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;
import com.team.wearly.domain.review.entity.enums.ReviewStatus;
import com.team.wearly.domain.review.service.SellerReviewService;
import com.team.wearly.domain.user.entity.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerReviewController {

    private final SellerReviewService sellerReviewService;

    /**
     * Authentication principal = Seller 엔티티 (JwtAuthenticationFilter에서 넣어줌)
     * - /api/seller/** 는 SecurityConfig에서 ROLE_SELLER만 접근 가능하게 막혀있음
     * - 그래서 여기서는 "Seller로 캐스팅"만 하면 sellerId를 바로 얻을 수 있음
     */
    private Seller getSeller(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Seller)) {
            // 여기 걸리면 토큰 role이 SELLER가 아니거나 filter에서 principal 세팅이 꼬인 것
            throw new IllegalStateException("SELLER 계정만 접근 가능합니다.");
        }
        return (Seller) principal;
    }

    /**
     * 1) 내 상품 리뷰 목록 조회
     */
    @GetMapping("/reviews")
    public ResponseEntity<Page<SellerReviewResponse.SellerReviewItemResponse>> getMyProductReviews(
            Authentication authentication,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) ReviewStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Seller seller = getSeller(authentication);

        // BaseTimeEntity 필드명이 createdDate일 때만 그대로 사용
        // 만약 createdAt이면 "createdAt"으로 바꿔야 함
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        return ResponseEntity.ok(
                sellerReviewService.getMyProductReviews(seller.getId(), productId, status, pageable)
        );
    }

    /**
     * 2) 내 상품 리뷰 요약 (평점 평균/리뷰 개수)
     */
    @GetMapping("/reviews/summary")
    public ResponseEntity<SellerReviewResponse.SellerReviewSummaryResponse> getMyReviewSummary(
            Authentication authentication,
            @RequestParam(required = false) Long productId
    ) {
        Seller seller = getSeller(authentication);

        return ResponseEntity.ok(
                sellerReviewService.getMyReviewSummary(seller.getId(), productId)
        );
    }

    /**
     * 3) 상품별 리뷰 요약 리스트 (상품 목록에 별점/리뷰수 붙일 때 사용)
     */
    @GetMapping("/reviews/summary/products")
    public ResponseEntity<List<ProductReviewSummaryResponse>> getProductReviewSummaries(
            Authentication authentication
    ) {
        Seller seller = getSeller(authentication);

        return ResponseEntity.ok(
                sellerReviewService.getProductReviewSummaries(seller.getId())
        );
    }

    /**
     * 4) 리뷰 신고 접수 (판매자)
     */
    @PostMapping("/reviews/{reviewId}/reports")
    public ResponseEntity<Void> reportReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @RequestBody SellerReviewRequest.ReviewReportCreateRequest request
    ) {
        Seller seller = getSeller(authentication);
        System.out.println("sellerId from token = " + seller.getId());
        Long reporterId = seller.getId(); // 최소 구현 (판매자 PK를 reporterId로 저장)
        sellerReviewService.reportReview(seller.getId(), reporterId, reviewId, request.reason());

        // 등록만 하고 응답 바디 필요 없으니 204 No Content
        return ResponseEntity.noContent().build();
    }

    /**
     * 5) 내가 접수한 신고 목록 조회 (판매자)
     */
    @GetMapping("/review-reports")
    public ResponseEntity<Page<SellerReviewReportItemResponse>> getMyReviewReports(
            Authentication authentication,
            @RequestParam(required = false) ReviewReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Seller seller = getSeller(authentication);

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        return ResponseEntity.ok(
                sellerReviewService.getMyReviewReports(seller.getId(), status, pageable)
        );
    }
}
