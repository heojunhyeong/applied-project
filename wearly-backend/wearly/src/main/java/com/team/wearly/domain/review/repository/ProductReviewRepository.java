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

    // 특정 상품의 리뷰를 페이징 처리해서 조회 // 상품 상세 리뷰 페이지용 (ACTIVE만 조회)
    @Query("""
        select r
        from ProductReview r
        where r.product.id = :productId
          and r.status = 'ACTIVE'
        """)
    Page<ProductReview> findByProductId(@Param("productId") Long productId, Pageable pageable);

    // 판매자 기준 리뷰 목록 조회 // productId/status 필터 선택 가능
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

    // 판매자 리뷰 요약(평균, 개수) // productId 있으면 해당 상품만, 없으면 전체
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

    // 판매자 상품별 리뷰 요약 리스트 // 상품별 평균/개수
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

    // 특정 리뷰가 판매자 소유 상품 리뷰인지 여부 // 신고/숨김 권한 체크용
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

    // 주문+상품 기준으로 리뷰 중복 작성 방지 // 한 주문의 한 상품에 리뷰 1개만
    Optional<ProductReview> findByReviewerIdAndOrderIdAndProductId(Long reviewerId, String orderId, Long productId);

    // 특정 상품 리뷰 최신순 조회 // 사용자 리뷰 리스트 화면용 (ACTIVE만 조회)
    @Query("""
        select r
        from ProductReview r
        where r.product.id = :productId
          and r.status = 'ACTIVE'
        order by r.createdDate desc
        """)
    List<ProductReview> findAllByProductIdOrderByCreatedDateDesc(@Param("productId") Long productId);
}
