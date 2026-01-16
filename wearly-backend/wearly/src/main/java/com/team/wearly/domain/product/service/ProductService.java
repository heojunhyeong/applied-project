package com.team.wearly.domain.product.service;

import com.team.wearly.domain.product.dto.request.ProductSearchCondition;
import com.team.wearly.domain.product.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.repository.ProductReviewRepository;
import com.team.wearly.domain.review.dto.response.ReviewResponse;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductReviewRepository reviewRepository;
    private final UserRepository userRepository;


    /**
     * 동적 검색 조건(브랜드, 카테고리 등)에 따라 상품 목록을 페이징하여 조회함
     *
     * @param condition 검색 필터 조건 객체
     * @param pageable 페이징 정보
     * @return 검색된 상품 응답 DTO 페이지
     * @author 김지번
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-16
     */
    public Page<SellerProductResponse> searchProducts(ProductSearchCondition condition, Pageable pageable) {
        return productRepository.search(condition, pageable)
                .map(SellerProductResponse::from);
    }

    /**
     * 특정 브랜드에 속한 상품들의 카테고리 목록을 중복 없이 조회함
     *
     * @param brand 조회할 브랜드 엔티티/열거형
     * @return 해당 브랜드가 보유한 카테고리 리스트
     * @author 김지번
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-16
     */
    public List<ProductCategory> getCategoriesByBrand(Brand brand) {
        return productRepository.findCategoriesByBrand(brand);
    }


    /**
     * 상품의 상세 정보와 함께 사용자 경험을 위해 최신 리뷰 5개를 미리보기 형태로 조회함
     *
     * @param productId 조회할 상품 식별자
     * @return 상품 정보와 리뷰 요약이 포함된 DTO
     * @author 김지번
     * @DateOfCreated 2026-01-16
     * @DateOfEdit 2026-01-16
     */
    // 1. 상품 상세 조회 (리뷰는 상위 5개만 미리보기)
    public SellerProductResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 삭제된 상품이면 접근 막기
        if (product.getStatus() == com.team.wearly.domain.product.entity.enums.ProductStatus.DELETED) {
            throw new IllegalArgumentException("판매가 중단된 상품입니다.");
        }

        // PageRequest를 사용하여 "첫 페이지의 5개"만 가져옵니다.
        Pageable limitFive = PageRequest.of(0, 5);
        Page<ProductReview> reviewPage = reviewRepository.findByProductId(productId, limitFive);
        List<ProductReview> reviews = reviewPage.getContent(); // 5개 리스트 추출

        // 닉네임 조회 및 DTO 변환 로직 (기존과 동일하지만, 대상이 5개로 줄어듦)
        List<Long> reviewerIds = reviews.stream().map(ProductReview::getReviewerId).distinct().toList();
        Map<Long, String> reviewerNicknames = userRepository.findAllById(reviewerIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUserNickname));

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> ReviewResponse.of(review, reviewerNicknames.getOrDefault(review.getReviewerId(), "알 수 없음")))
                .toList();

        return SellerProductResponse.of(product, reviewResponses);
    }

    /**
     * 상품 상세 페이지에서 리뷰 전체를 확인하기 위해 페이징 처리가 된 리뷰 목록을 조회함
     *
     * @param productId 대상 상품 식별자
     * @param pageable 페이징 및 정렬 정보
     * @return 닉네임 정보가 포함된 리뷰 응답 DTO 페이지
     * @author 김지번
     * @DateOfCreated 2026-01-16
     * @DateOfEdit 2026-01-16
     */
    // 2. 리뷰 더보기 API용 (페이징 지원)
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        // 해당 상품의 리뷰를 페이징으로 가져옴
        Page<ProductReview> reviewPage = reviewRepository.findByProductId(productId, pageable);

        // 닉네임 매핑 (페이지에 있는 리뷰들만)
        List<Long> reviewerIds = reviewPage.getContent().stream().map(ProductReview::getReviewerId).distinct().toList();
        Map<Long, String> reviewerNicknames = userRepository.findAllById(reviewerIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUserNickname));

        // DTO로 변환하여 반환
        return reviewPage.map(review -> ReviewResponse.of(
                review,
                reviewerNicknames.getOrDefault(review.getReviewerId(), "알 수 없음")
        ));
    }
}
