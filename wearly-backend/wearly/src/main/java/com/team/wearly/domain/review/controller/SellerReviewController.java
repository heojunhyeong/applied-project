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
     * Authentication principal에서 Seller를 꺼냄 // SELLER 전용 API 보안 보조
     */
    private Seller getSeller(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Seller)) {
            // 토큰 role이 SELLER가 아니거나 filter principal 세팅이 꼬인 경우
            throw new IllegalStateException("SELLER 계정만 접근 가능합니다.");
        }
        return (Seller) principal;
    }

    /**
     * 판매자 상품 리뷰 목록 조회 // 최신순 페이징 + 옵션 필터(productId/status)
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

        // createdDate 정렬 // BaseTimeEntity 필드명이 createdDate일 때만 유효
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        return ResponseEntity.ok(
                sellerReviewService.getMyProductReviews(seller.getId(), productId, status, pageable)
        );
    }

    /**
     * 판매자 리뷰 요약(평균/개수) 조회 // 전체 or 특정 상품(productId)
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
     * 판매자 상품별 리뷰 요약 리스트 조회 // 상품별 평균/개수
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
     * 리뷰 신고 접수 // reviewId + 신고 사유로 report 생성
     */
    @PostMapping("/reviews/{reviewId}/reports")
    public ResponseEntity<Void> reportReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @RequestBody SellerReviewRequest.ReviewReportCreateRequest request
    ) {
        Seller seller = getSeller(authentication);

        // 신고자 ID 설정 // 최소 구현: 판매자 PK를 reporterId로 저장
        Long reporterId = seller.getId();

        // 신고 처리 호출 // 권한 체크 + 중복 체크 + 저장
        sellerReviewService.reportReview(seller.getId(), reporterId, reviewId, request.reason());

        return ResponseEntity.noContent().build();
    }

    /**
     * 판매자 신고 내역 조회 // 상태 필터 + 최신순 페이징
     */
    @GetMapping("/review-reports")
    public ResponseEntity<Page<SellerReviewReportItemResponse>> getMyReviewReports(
            Authentication authentication,
            @RequestParam(required = false) ReviewReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Seller seller = getSeller(authentication);

        // createdDate 정렬 // BaseTimeEntity 필드명이 createdDate일 때만 유효
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        return ResponseEntity.ok(
                sellerReviewService.getMyReviewReports(seller.getId(), status, pageable)
        );
    }
}
