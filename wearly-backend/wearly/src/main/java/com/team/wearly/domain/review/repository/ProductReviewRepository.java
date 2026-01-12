package com.team.wearly.domain.review.repository;

import com.team.wearly.domain.review.dto.response.ProductReviewSummaryResponse;
import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.entity.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    // 1) 리뷰 목록: seller 기준 + (옵션) productId + (옵션) status
    @Query("""
        select r
        from ProductReview r
        join r.product p
        where p.sellerId = :sellerId
          and (:productId is null or p.id = :productId)
          and (:status is null or r.status = :status)
        """)
    Page<ProductReview> findSellerReviews(
            @Param("sellerId") Long sellerId,
            @Param("productId") Long productId,
            @Param("status") ReviewStatus status,
            Pageable pageable
    );

    // 2) 요약: seller 기준 + (옵션) productId
    @Query("""
        select coalesce(avg(r.rating), 0), count(r)
        from ProductReview r
        join r.product p
        where p.sellerId = :sellerId
          and (:productId is null or p.id = :productId)
        """)
    Object[] getSellerReviewSummary(
            @Param("sellerId") Long sellerId,
            @Param("productId") Long productId
    );

    // 3) 상품별 요약 리스트 (DTO 분리 버전)
    @Query("""
        select new com.team.wearly.domain.review.dto.response.ProductReviewSummaryResponse(
            p.id,
            coalesce(avg(r.rating), 0),
            count(r)
        )
        from ProductReview r
        join r.product p
        where p.sellerId = :sellerId
        group by p.id
        """)
    List<ProductReviewSummaryResponse> getProductReviewSummaries(
            @Param("sellerId") Long sellerId
    );


    @Query("""
    select count(pr) > 0
    from ProductReview pr
    join pr.product p
    where pr.id = :reviewId
      and p.sellerId = :sellerId
""")
    boolean existsSellerReview(
            @Param("reviewId") Long reviewId,
            @Param("sellerId") Long sellerId
    );

    Optional<ProductReview> findByReviewerIdAndOrderIdAndProductId(Long reviewerId, String orderId, Long productId);
    List<ProductReview> findAllByProductIdOrderByCreatedDateDesc(Long productId);
}
