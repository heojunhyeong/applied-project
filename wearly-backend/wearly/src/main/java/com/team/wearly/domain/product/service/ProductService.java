package com.team.wearly.domain.product.service;

import com.team.wearly.domain.product.dto.request.ProductSearchCondition;
import com.team.wearly.domain.product.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import com.team.wearly.domain.product.repository.ProductRepository;
import com.team.wearly.domain.review.dto.response.ReviewResponse;
import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.repository.ProductReviewRepository;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     */
    public Page<SellerProductResponse> searchProducts(ProductSearchCondition condition, Pageable pageable) {
        return productRepository.search(condition, pageable)
                .map(SellerProductResponse::from);
    }

    /**
     * 특정 브랜드에 속한 상품들의 카테고리 목록을 중복 없이 조회함
     */
    public List<ProductCategory> getCategoriesByBrand(Brand brand) {
        return productRepository.findCategoriesByBrand(brand);
    }

    /**
     * 상품 상세 조회 (리뷰 5개 미리보기 포함)
     * - SELLER 로그인 상태면 isMyProduct=true/false 계산해서 내려줌
     */
    public SellerProductResponse getProductDetail(Authentication authentication, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다. productId=" + productId));

        // 삭제된 상품이면 접근 막기
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("판매가 중단된 상품입니다.");
        }

        // ✅ SELLER 로그인 상태면 내 상품인지 판단
        boolean isMyProduct = isMyProduct(authentication, product);

        // 리뷰 5개 미리보기
        Pageable limitFive = PageRequest.of(0, 5);
        Page<ProductReview> reviewPage = reviewRepository.findByProductId(productId, limitFive);
        List<ProductReview> reviews = reviewPage.getContent();

        // 닉네임 매핑
        List<Long> reviewerIds = reviews.stream()
                .map(ProductReview::getReviewerId)
                .distinct()
                .toList();

        Map<Long, String> reviewerNicknames = userRepository.findAllById(reviewerIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUserNickname));

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> ReviewResponse.of(
                        review,
                        reviewerNicknames.getOrDefault(review.getReviewerId(), "알 수 없음")
                ))
                .toList();

        // ✅ isMyProduct 포함해서 반환하도록 DTO of 메서드 수정/추가 필요
        return SellerProductResponse.of(product, reviewResponses, isMyProduct);
    }

    /**
     * 상품 상세 페이지 리뷰 전체 조회(페이징)
     */
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        Page<ProductReview> reviewPage = reviewRepository.findByProductId(productId, pageable);

        List<Long> reviewerIds = reviewPage.getContent().stream()
                .map(ProductReview::getReviewerId)
                .distinct()
                .toList();

        Map<Long, String> reviewerNicknames = userRepository.findAllById(reviewerIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUserNickname));

        return reviewPage.map(review -> ReviewResponse.of(
                review,
                reviewerNicknames.getOrDefault(review.getReviewerId(), "알 수 없음")
        ));
    }

    /**
     * authentication에서 Seller인지 확인하고, product.sellerId와 비교해서 내 상품 여부 판단
     */
    private boolean isMyProduct(Authentication authentication, Product product) {
        if (authentication == null) return false;

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Seller seller)) return false;

        Long sellerId = seller.getId();
        if (sellerId == null) return false;

        return sellerId.equals(product.getSellerId());
    }
}
