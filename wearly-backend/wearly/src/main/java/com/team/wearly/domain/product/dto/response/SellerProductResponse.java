package com.team.wearly.domain.product.dto.response;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import com.team.wearly.domain.product.entity.enums.Size;
import com.team.wearly.domain.review.dto.response.ReviewResponse; // [중요] 리뷰 DTO import

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record SellerProductResponse(
        Long id,
        Long sellerId,
        String productName,
        Long price,
        Long stockQuantity,
        String description,
        String imageUrl,
        Brand brand,
        ProductCategory productCategory,
        Set<Size> availableSizes,
        ProductStatus status,
        LocalDateTime createdDate,
        LocalDateTime updatedDate,

        // 리뷰 관련 필드 3개 추가
        List<ReviewResponse> reviews, // 리뷰 리스트
        Double averageRating,         // 평균 평점
        int reviewCount               // 리뷰 개수
) {
    /**
     * 리뷰 없이 상품 정보만 변환할 때 (목록 조회 등에서 사용)
     */
    public static SellerProductResponse from(Product p) {
        // 리뷰가 없는 경우 빈 리스트로 처리
        return of(p, List.of());
    }

    /**
     * 상품 정보 + 리뷰 리스트를 함께 변환할 때 (상세 조회에서 사용)
     * ProductService에서 호출
     */
    public static SellerProductResponse of(Product p, List<ReviewResponse> reviews) {
        // 1. 평균 평점 계산 (리뷰가 없으면 0.0)
        double avg = reviews.isEmpty() ? 0.0 :
                reviews.stream()
                        .mapToInt(ReviewResponse::rating)
                        .average()
                        .orElse(0.0);

        // 2. 소수점 한 자리까지 반올림 (예: 4.333 -> 4.3)
        avg = Math.round(avg * 10.0) / 10.0;

        // DB 상태가 판매중(ON_SALE)이라도, 재고가 없으면 품절(SOLD_OUT)상태로 보여줌
        ProductStatus displayStatus = p.getStatus();
        if (p.getStockQuantity() <= 0) {
            displayStatus = ProductStatus.SOLD_OUT;
        }

        return new SellerProductResponse(
                p.getId(),
                p.getSellerId(),
                p.getProductName(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getDescription(),
                p.getImageUrl(),
                p.getBrand(),
                p.getProductCategory(),
                p.getAvailableSizes(),
                p.getStatus(),
                p.getCreatedDate(),
                p.getUpdatedDate(),
                reviews,       // 여기에 리뷰 리스트 주입
                avg,           // 계산된 평균 평점 주입
                reviews.size() // 리뷰 개수 주입
        );
    }
}
