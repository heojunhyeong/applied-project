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
     *
     * @param sellerId 판매자 식별자
     * @param productId 특정 상품 식별자 (null일 경우 전체 상품 대상)
     * @param status 리뷰 노출 상태 (null일 경우 전체 상태 대상)
     * @param pageable 페이징 정보
     * @return 리뷰 항목 응답 DTO 페이지
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public Page<SellerReviewResponse.SellerReviewItemResponse> getMyProductReviews(
            Long sellerId,
            Long productId,
            ReviewStatus status,
            Pageable pageable
    ) {
        Page<ProductReview> page = productReviewRepository.findSellerReviews(sellerId, productId, status, pageable);

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
     *
     * @param sellerId 판매자 식별자
     * @param productId 특정 상품 식별자 (null일 경우 전체 통계)
     * @return 평균 평점과 개수가 포함된 요약 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public SellerReviewResponse.SellerReviewSummaryResponse getMyReviewSummary(Long sellerId, Long productId) {
        Object[] row = productReviewRepository.getSellerReviewSummary(sellerId, productId);

        double avg = 0.0;
        long cnt = 0L;

        if (row != null) {
            if (row[0] != null) avg = ((Number) row[0]).doubleValue();
            if (row[1] != null) cnt = ((Number) row[1]).longValue();
        }

        return new SellerReviewResponse.SellerReviewSummaryResponse(avg, cnt);
    }

    /**
     * 판매자가 등록한 각 상품별로 리뷰 통계(평균 별점, 리뷰 개수) 리스트를 조회함
     *
     * @param sellerId 판매자 식별자
     * @return 상품별 리뷰 요약 정보 리스트
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public List<ProductReviewSummaryResponse> getProductReviewSummaries(Long sellerId) {
        return productReviewRepository.getProductReviewSummaries(sellerId);
    }

    /**
     * 특정 리뷰가 본인 상품의 리뷰인지 확인하고, 중복 신고 여부를 체크한 뒤 관리자에게 신고 내역을 접수함
     *
     * @param sellerId 판매자 식별자
     * @param reporterId 신고 주체 식별자
     * @param reviewId 신고 대상 리뷰 식별자
     * @param reason 신고 사유 (비방, 허위 사실 등)
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @Transactional
    public void reportReview(Long sellerId, Long reporterId, Long reviewId, ReviewReportReason reason) {
        if (reason == null) throw new ResponseStatusException(BAD_REQUEST, "reason은 필수");

        boolean exists = productReviewRepository.existsSellerReview(reviewId, sellerId);

        if (!exists) {
            throw new ResponseStatusException(FORBIDDEN, "본인 상품 리뷰만 신고 접수 가능");
        }

        if (reviewReportRepository.existsByReviewIdAndReporterId(reviewId, reporterId)) {
            throw new ResponseStatusException(CONFLICT, "이미 신고 접수한 리뷰");
        }

        ReviewReport report = ReviewReport.builder()
                .reviewId(reviewId)
                .reporterId(reporterId)
                .reason(reason)
                .build();

        reviewReportRepository.save(report);
    }

    /**
     * 판매자가 이전에 접수한 리뷰 신고들의 진행 상태(접수, 반려, 승인 등)를 조회함
     *
     * @param sellerId 판매자 식별자
     * @param status 신고 처리 상태 필터 (Optional)
     * @param pageable 페이징 정보
     * @return 신고 내역 항목 DTO 페이지
     * @author 허보미
     * @DateOfCreated 2026-01-16
     * @DateOfEdit 2026-01-16
     */
    public Page<SellerReviewReportItemResponse> getMyReviewReports(
            Long sellerId,
            ReviewReportStatus status,
            Pageable pageable
    ) {
        return reviewReportRepository.findSellerReviewReports(sellerId, status, pageable);
    }
}
