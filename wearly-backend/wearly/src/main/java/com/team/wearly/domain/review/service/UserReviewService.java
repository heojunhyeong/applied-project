package com.team.wearly.domain.review.service;

import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.repository.ProductRepository;
import com.team.wearly.domain.review.dto.request.UserReviewRequest;
import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.entity.enums.ReviewStatus;
import com.team.wearly.domain.review.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;


    /**
     * 특정 주문과 상품에 대한 리뷰를 생성하며 초기 상태를 'ACTIVE'로 설정함
     *
     * @param userId 리뷰를 작성하는 사용자 식별자
     * @param request 상품 ID, 주문 ID, 별점, 내용 등이 포함된 요청 DTO
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2026-01-11
     */
    @Transactional
    public void createReview(Long userId, UserReviewRequest.Create request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "상품을 찾을 수 없습니다."));

        ProductReview review = ProductReview.builder()
                .reviewerId(userId)
                .orderId(request.orderId())
                .product(product)
                .rating(request.rating())
                .content(request.content())
                .status(ReviewStatus.ACTIVE)
                .build();

        productReviewRepository.save(review);
    }

    /**
     * 본인이 작성한 리뷰인지 확인한 후 시스템에서 리뷰 데이터를 완전히 삭제함
     *
     * @param userId 삭제를 요청한 사용자 식별자
     * @param reviewId 삭제 대상 리뷰 식별자
     * @author 허준형
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (!review.getReviewerId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "본인의 리뷰만 삭제할 수 있습니다.");
        }

        productReviewRepository.delete(review);
    }

    /**
     * 특정 상품에 작성된 모든 리뷰를 최신순으로 정렬하여 조회함
     *
     * @param productId 대상 상품 식별자
     * @return 최신순으로 정렬된 리뷰 엔티티 리스트
     * @author 허준형
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public List<ProductReview> getReviewsByProduct(Long productId) {
        return productReviewRepository.findAllByProductIdOrderByCreatedDateDesc(productId);
    }
}