package com.team.wearly.domain.review.service;

import com.team.wearly.domain.review.dto.response.AdminReviewReportResponse;
import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.entity.ReviewReport;
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewService {

    private final ReviewReportRepository reviewReportRepository;
    private final ProductReviewRepository productReviewRepository;

    /**
     * 관리자가 신고된 리뷰 목록을 조회함 (상태 필터링 지원)
     *
     * @param status 신고 상태 필터 (PENDING, RESOLVED, REJECTED)
     * @param pageable 페이징 정보
     * @return 신고된 리뷰 목록 (페이지)
     */
    public Page<AdminReviewReportResponse> getReviewReports(
            ReviewReportStatus status,
            Pageable pageable
    ) {
        return reviewReportRepository.findAdminReviewReports(status, pageable);
    }

    /**
     * 관리자가 신고를 승인 처리함
     * - ReviewReportStatus를 RESOLVED로 변경
     * - ProductReview의 status를 HIDDEN으로 변경하여 리뷰 숨김 처리
     *
     * @param reportId 승인할 신고의 식별자
     */
    @Transactional
    public void approveReport(Long reportId) {
        // 신고 엔티티 조회
        ReviewReport report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "신고를 찾을 수 없습니다."));

        // 이미 처리된 신고인지 확인
        if (report.getStatus() != ReviewReportStatus.PENDING) {
            throw new ResponseStatusException(BAD_REQUEST, "이미 처리된 신고입니다.");
        }

        // 신고 승인 처리 (RESOLVED 상태로 변경)
        report.resolve();

        // 해당 리뷰를 숨김 처리 (HIDDEN 상태로 변경)
        ProductReview review = report.getReview();
        review.hide();

        // 저장
        reviewReportRepository.save(report);
        productReviewRepository.save(review);
    }

    /**
     * 관리자가 신고를 반려 처리함
     * - ReviewReportStatus를 REJECTED로 변경
     * - ProductReview의 status는 그대로 유지 (ACTIVE 상태 유지)
     *
     * @param reportId 반려할 신고의 식별자
     */
    @Transactional
    public void rejectReport(Long reportId) {
        // 신고 엔티티 조회
        ReviewReport report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "신고를 찾을 수 없습니다."));

        // 이미 처리된 신고인지 확인
        if (report.getStatus() != ReviewReportStatus.PENDING) {
            throw new ResponseStatusException(BAD_REQUEST, "이미 처리된 신고입니다.");
        }

        // 신고 반려 처리 (REJECTED 상태로 변경)
        report.reject();

        // 리뷰는 그대로 유지 (ACTIVE 상태 유지)

        // 저장
        reviewReportRepository.save(report);
    }
}
