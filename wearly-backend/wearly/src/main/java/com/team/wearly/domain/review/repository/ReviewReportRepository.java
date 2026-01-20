package com.team.wearly.domain.review.repository;

import com.team.wearly.domain.review.dto.response.AdminReviewReportResponse;
import com.team.wearly.domain.review.dto.response.SellerReviewReportItemResponse;
import com.team.wearly.domain.review.entity.ReviewReport;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    // 동일 신고 중복 방지 // 같은 reporter가 같은 review를 재신고 못하게
    boolean existsByReview_IdAndReporterId(Long reviewId, Long reporterId);

    // 판매자 기준 신고 목록 조회 // 상태 필터 선택 가능
    @Query("""
        select new com.team.wearly.domain.review.dto.response.SellerReviewReportItemResponse(
            rr.id,
            rr.review.id,
            rr.review.product.id,
            rr.reporterId,
            rr.reason,
            rr.status,
            rr.createdDate
        )
        from ReviewReport rr
        join rr.review pr
        join pr.product p
        where p.sellerId = :sellerId
          and (:status is null or rr.status = :status)
        """)
    Page<SellerReviewReportItemResponse> findSellerReviewReports(
            @Param("sellerId") Long sellerId,
            @Param("status") ReviewReportStatus status,
            Pageable pageable
    );

    // 관리자 기준 신고 목록 조회 // 상태 필터 선택 가능, 전체 신고 내역 조회
    @Query("""
        select new com.team.wearly.domain.review.dto.response.AdminReviewReportResponse(
            rr.id,
            rr.review.id,
            rr.review.product.id,
            rr.review.reviewerId,
            COALESCE(reviewerUser.userName, reviewerSeller.userName),
            rr.reporterId,
            COALESCE(reporterUser.userName, reporterSeller.userName),
            rr.review.content,
            rr.review.rating,
            rr.review.status,
            rr.reason,
            rr.status,
            rr.createdDate,
            rr.review.createdDate
        )
        from ReviewReport rr
        join rr.review pr
        left join com.team.wearly.domain.user.entity.User reviewerUser 
            on reviewerUser.id = rr.review.reviewerId and reviewerUser.deletedAt is null
        left join com.team.wearly.domain.user.entity.Seller reviewerSeller 
            on reviewerSeller.id = rr.review.reviewerId and reviewerSeller.deletedAt is null
        left join com.team.wearly.domain.user.entity.User reporterUser 
            on reporterUser.id = rr.reporterId and reporterUser.deletedAt is null
        left join com.team.wearly.domain.user.entity.Seller reporterSeller 
            on reporterSeller.id = rr.reporterId and reporterSeller.deletedAt is null
        where (:status is null or rr.status = :status)
        """)
    Page<AdminReviewReportResponse> findAdminReviewReports(
            @Param("status") ReviewReportStatus status,
            Pageable pageable
    );
}
