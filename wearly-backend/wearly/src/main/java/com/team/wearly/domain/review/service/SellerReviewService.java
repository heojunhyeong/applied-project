package com.team.wearly.domain.review.service;

import com.team.wearly.domain.review.dto.response.ProductReviewSummaryResponse;
import com.team.wearly.domain.review.dto.response.SellerReviewReportItemResponse;
import com.team.wearly.domain.review.dto.response.SellerReviewResponse;
import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.entity.ReviewReport;
import com.team.wearly.domain.review.entity.enums.ReviewReportReason;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;
import com.team.wearly.domain.review.entity.enums.ReviewStatus;
import com.team.wearly.domain.review.repository.ProductReviewRepository;
import com.team.wearly.domain.review.repository.ReviewReportRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerReviewService {

    private static final Logger logger = LoggerFactory.getLogger(SellerReviewService.class);

    private final ProductReviewRepository productReviewRepository;
    private final ReviewReportRepository reviewReportRepository;

    /**
     * 판매자가 소유한 상품들에 달린 리뷰 목록을 조회함 (상품별, 상태별 필터링 지원)
     */
    public Page<SellerReviewResponse.SellerReviewItemResponse> getMyProductReviews(
            Long sellerId,
            Long productId,
            ReviewStatus status,
            Pageable pageable
    ) {
        // 판매자 소유 상품 리뷰 목록 조회 // productId/status 필터 적용
        Page<ProductReview> page = productReviewRepository.findSellerReviews(sellerId, productId, status, pageable);

        // 엔티티 -> DTO 매핑 // 프론트 응답 형태로 변환
        return page.map(r -> new SellerReviewResponse.SellerReviewItemResponse(
                r.getId(),
                r.getProduct().getId(),
                r.getReviewerId(),
                r.getOrderId(),
                r.getRating(),
                r.getContent(),
                r.getStatus(),
                r.getCreatedDate()
        ));
    }

    /**
     * 판매자의 전체 상품 혹은 특정 상품의 평균 별점과 총 리뷰 수를 집계하여 반환함
     */
    public SellerReviewResponse.SellerReviewSummaryResponse getMyReviewSummary(Long sellerId, Long productId) {
        // 평균/개수 집계 쿼리 실행 // (avg, count) 반환
        Object[] row = productReviewRepository.getSellerReviewSummary(sellerId, productId);

        double avg = 0.0;
        long cnt = 0L;

        // 결과 파싱 // null 안전 처리
        if (row != null) {
            if (row[0] != null) avg = ((Number) row[0]).doubleValue();
            if (row[1] != null) cnt = ((Number) row[1]).longValue();
        }

        return new SellerReviewResponse.SellerReviewSummaryResponse(avg, cnt);
    }

    /**
     * 판매자가 등록한 각 상품별로 리뷰 통계(평균 별점, 리뷰 개수) 리스트를 조회함
     */
    public List<ProductReviewSummaryResponse> getProductReviewSummaries(Long sellerId) {
        // 판매자 상품별 리뷰 요약 리스트 조회 // group by product
        return productReviewRepository.getProductReviewSummaries(sellerId);
    }

    /**
     * 특정 리뷰가 본인 상품의 리뷰인지 확인하고, 중복 신고 여부를 체크한 뒤 관리자에게 신고 내역을 접수함
     */
    @Transactional
    public void reportReview(Long sellerId, Long reporterId, Long reviewId, ReviewReportReason reason) {
        // 신고 사유 필수 체크 // null이면 400
        if (reason == null) throw new ResponseStatusException(BAD_REQUEST, "reason은 필수");

        // 본인 상품 리뷰인지 권한 체크 // 남의 상품 리뷰 신고 불가
        boolean exists = productReviewRepository.existsSellerReview(reviewId, sellerId);
        if (!exists) throw new ResponseStatusException(FORBIDDEN, "본인 상품 리뷰만 신고 접수 가능");

        // 중복 신고 방지 // 같은 신고자가 같은 리뷰를 또 신고 못함
        if (reviewReportRepository.existsByReview_IdAndReporterId(reviewId, reporterId)) {
            throw new ResponseStatusException(CONFLICT, "이미 신고 접수한 리뷰");
        }

        // 신고 대상 리뷰 엔티티 로드 // 연관관계 저장용
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "리뷰가 존재하지 않습니다."));

        // 신고 엔티티 생성 // reviewId 대신 review 연관관계로 저장
        ReviewReport report = ReviewReport.builder()
                .review(review)
                .reporterId(reporterId)
                .reason(reason)
                .build();

        // 신고 저장 // PENDING 기본값 유지
        reviewReportRepository.save(report);
    }

    /**
     * 판매자가 이전에 접수한 리뷰 신고들의 진행 상태(접수, 반려, 승인 등)를 조회함
     */
    public Page<SellerReviewReportItemResponse> getMyReviewReports(
            Long sellerId,
            ReviewReportStatus status,
            Pageable pageable
    ) {
        // 판매자 기준 신고 목록 조회 // 상태 필터(optional) + 페이징
        return reviewReportRepository.findSellerReviewReports(sellerId, status, pageable);
    }
}
