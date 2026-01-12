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

    private final ProductReviewRepository productReviewRepository;
    private final ReviewReportRepository reviewReportRepository;

    // 1) 내 상품 리뷰 목록 조회 (옵션: productId, status)
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

    // 2) 요약 (productId 없으면 전체 / 있으면 해당 상품만)
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

    // 3) 상품별 요약 리스트
    public List<ProductReviewSummaryResponse> getProductReviewSummaries(Long sellerId) {
        return productReviewRepository.getProductReviewSummaries(sellerId);
    }

    // 4) 리뷰 신고 접수(판매자)
    @Transactional
    public void reportReview(Long sellerId, Long reporterId, Long reviewId, ReviewReportReason reason) {
        if (reason == null) throw new ResponseStatusException(BAD_REQUEST, "reason은 필수");

        boolean exists = productReviewRepository.existsSellerReview(reviewId, sellerId);

        System.out.println("====== REVIEW REPORT DEBUG ======");
        System.out.println("sellerId(from token) = " + sellerId);
        System.out.println("reviewId(path)       = " + reviewId);
        System.out.println("existsSellerReview   = " + exists);
        System.out.println("=================================");

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

    // 5) 내가 접수한 신고 목록 조회(판매자)
    public Page<SellerReviewReportItemResponse> getMyReviewReports(
            Long sellerId,
            ReviewReportStatus status,
            Pageable pageable
    ) {
        return reviewReportRepository.findSellerReviewReports(sellerId, status, pageable);
    }
}
