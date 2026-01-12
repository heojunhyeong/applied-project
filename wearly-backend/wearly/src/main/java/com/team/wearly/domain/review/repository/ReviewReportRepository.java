package com.team.wearly.domain.review.repository;

import com.team.wearly.domain.review.dto.response.SellerReviewReportItemResponse;
import com.team.wearly.domain.review.entity.ReviewReport;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    boolean existsByReviewIdAndReporterId(Long reviewId, Long reporterId);

    @Query("""
        select new com.team.wearly.domain.review.dto.response.SellerReviewReportItemResponse(
            rr.id,
            rr.reviewId,
            pr.product.id,
            rr.reporterId,
            rr.reason,
            rr.status,
            rr.createdDate
        )
        from ReviewReport rr
        join ProductReview pr on pr.id = rr.reviewId
        join pr.product p
        where p.sellerId = :sellerId
          and (:status is null or rr.status = :status)
        """)
    Page<SellerReviewReportItemResponse> findSellerReviewReports(
            @Param("sellerId") Long sellerId,
            @Param("status") ReviewReportStatus status,
            Pageable pageable
    );
}
