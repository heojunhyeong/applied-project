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
     * SecurityContext의 Authentication 객체에서 현재 로그인한 판매자 정보를 추출함
     * Authentication principal = Seller 엔티티 (JwtAuthenticationFilter에서 넣어줌)
     * - /api/seller/** 는 SecurityConfig에서 ROLE_SELLER만 접근 가능하게 막혀있음
     * - 그래서 여기서는 "Seller로 캐스팅"만 하면 sellerId를 바로 얻을 수 있음
     * @param authentication 인증 정보 객체
     * @return 캐스팅된 Seller 엔티티
     * @throws IllegalStateException 판매자 권한이 아닌 경우 발생
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
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
     * 판매자가 보유한 전체 상품 또는 특정 상품의 리뷰 목록을 최신순으로 페이징 조회함
     *
     * @param authentication 인증 정보
     * @param productId 특정 상품 필터링용 식별자 (Optional)
     * @param status 리뷰 상태 필터 (Optional)
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 리뷰 목록 응답 DTO 페이지
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
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
     * 판매자의 전체 상품 또는 특정 상품에 대한 평균 평점과 리뷰 총 개수 요약을 조회함
     *
     * @param authentication 인증 정보
     * @param productId 특정 상품 식별자 (Optional)
     * @return 평점 및 개수 요약 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
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
     * 판매자가 등록한 모든 상품별 리뷰 통계(별점, 리뷰수) 리스트를 조회함
     *
     * @param authentication 인증 정보
     * @return 상품별 리뷰 요약 정보 리스트
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
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
     * 부적절한 리뷰에 대해 사유를 기재하여 관리자에게 신고를 접수함
     *
     * @param authentication 인증 정보
     * @param reviewId 신고 대상 리뷰 식별자
     * @param request 신고 사유가 포함된 요청 DTO
     * @return 성공 시 204 No Content
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @PostMapping("/reviews/{reviewId}/reports")
    public ResponseEntity<Void> reportReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @RequestBody SellerReviewRequest.ReviewReportCreateRequest request
    ) {
        Seller seller = getSeller(authentication);
        Long reporterId = seller.getId(); // 최소 구현 (판매자 PK를 reporterId로 저장)
        sellerReviewService.reportReview(seller.getId(), reporterId, reviewId, request.reason());

        // 등록만 하고 응답 바디 필요 없으니 204 No Content
        return ResponseEntity.noContent().build();
    }

    /**
     * 판매자 본인이 접수한 리뷰 신고 내역과 처리 상태를 페이징 조회함
     *
     * @param authentication 인증 정보
     * @param status 처리 상태 필터 (Optional)
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 신고 내역 응답 DTO 페이지
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
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
