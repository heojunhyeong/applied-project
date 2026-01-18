package com.team.wearly.domain.review.controller;

import com.team.wearly.domain.review.dto.response.AdminReviewReportResponse;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;
import com.team.wearly.domain.review.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    /**
     * 관리자가 신고된 리뷰 목록을 조회함
     * - 상태 필터링 지원 (PENDING, RESOLVED, REJECTED)
     * - 최신순 페이징
     *
     * @param status 신고 상태 필터 (optional)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 신고된 리뷰 목록 (페이지)
     */
    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<AdminReviewReportResponse>> getReviewReports(
            @RequestParam(required = false) ReviewReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 신고 최신순 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<AdminReviewReportResponse> response = adminReviewService.getReviewReports(status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자가 신고를 승인 처리함
     * - ReviewReportStatus를 RESOLVED로 변경
     * - ProductReview의 status를 HIDDEN으로 변경하여 리뷰 숨김 처리
     *
     * @param reportId 승인할 신고의 식별자
     * @return 성공 시 204 No Content
     */
    @PatchMapping("/reports/{reportId}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> approveReport(@PathVariable Long reportId) {
        adminReviewService.approveReport(reportId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자가 신고를 반려 처리함
     * - ReviewReportStatus를 REJECTED로 변경
     * - ProductReview의 status는 그대로 유지 (ACTIVE 상태 유지)
     *
     * @param reportId 반려할 신고의 식별자
     * @return 성공 시 204 No Content
     */
    @PatchMapping("/reports/{reportId}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> rejectReport(@PathVariable Long reportId) {
        adminReviewService.rejectReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
