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

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (!review.getReviewerId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "본인의 리뷰만 삭제할 수 있습니다.");
        }

        productReviewRepository.delete(review);
    }

    public List<ProductReview> getReviewsByProduct(Long productId) {
        return productReviewRepository.findAllByProductIdOrderByCreatedDateDesc(productId);
    }
}