package com.team.wearly.domain.order.service;

import com.team.wearly.domain.coupon.entity.UserCoupon;
import com.team.wearly.domain.coupon.entity.enums.CouponType;
import com.team.wearly.domain.coupon.repository.UserCouponRepository;
import com.team.wearly.domain.order.dto.response.OrderDetailResponse;
import com.team.wearly.domain.order.dto.response.OrderHistoryResponse;
import com.team.wearly.domain.order.entity.Cart;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDelivery;
import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.CartRepository;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.repository.ProductRepository;
import com.team.wearly.domain.review.entity.ProductReview;
import com.team.wearly.domain.review.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final ProductReviewRepository productReviewRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserCouponRepository  userCouponRepository;

    /**
     * 주문 로직
     * 주문번호는 현재 날짜와 UUID를 조합하며 생성되며
     * 최초 주문 상태는 {@link OrderStatus#BEFORE_PAID} 로 설정된다
     * 단일 주문 상품 정보(사이트에서 주문하기 클릭)시 바로 구매로 우선 처리하고
     * 단일 주문 상품 정보가 없을 시 장바구니 구매로 처리한다
     *
     * TODO: 주석 보완 필요
     * @param request 주문 생성을 위한 요청 객체
     *                - userId: 주문을 생성하는 사용자 ID
     * @return 생성되어 저장된 Order 엔티티
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2025-01-11
     */
    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId가 null입니다.");
        }

        String orderId = "ORD-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8);

        Long discountPrice = 0L;
        UserCoupon targetCoupon = null;

        if (request.getUserCouponId() != null) {
            // 유저가 보유한 쿠폰 조회
            targetCoupon = userCouponRepository.findByIdAndUserId(request.getUserCouponId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

            // 유효성 체크 (상태, 만료일, 최소주문금액)
            if (!targetCoupon.isAvailable(request.getTotalPrice())) {
                throw new IllegalArgumentException("사용 조건이 맞지 않거나 유효하지 않은 쿠폰입니다.");
            }

            // 쿠폰 타입별 할인 계산
            if (targetCoupon.getCouponType() == CouponType.DISCOUNT_AMOUNT) {
                discountPrice = targetCoupon.getDiscountValue();
            } else if (targetCoupon.getCouponType() == CouponType.DISCOUNT_RATE) {
                discountPrice = request.getTotalPrice() * targetCoupon.getDiscountValue() / 100;
            }

            // 쿠폰 사용 처리 및 주문번호 기록
            targetCoupon.applyToOrder(orderId);
        }

        Order order = Order.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPrice(request.getTotalPrice())
                // 쿠폰 할인 반영
                .couponDiscountPrice(discountPrice)
                .orderStatus(OrderStatus.BEFORE_PAID)
                .build();

        // 주문 상세 설정 로직
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. ID: " + request.getProductId()));

            if (product.getStockQuantity() < request.getQuantity()) {
                throw new IllegalStateException("상품 재고가 부족합니다.");
            }

            OrderDetail detail = OrderDetail.builder()
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .product(product)
                    .build();
            order.addOrderDetail(detail);

        } else if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            List<Cart> selectedCarts = cartRepository.findAllById(request.getCartItemIds());

            if (selectedCarts.isEmpty()) {
                throw new IllegalArgumentException("장바구니 항목이 존재하지 않습니다.");
            }

            for (Cart cart : selectedCarts) {
                OrderDetail detail = OrderDetail.builder()
                        .quantity(cart.getQuantity())
                        .price(cart.getProduct().getPrice())
                        .product(cart.getProduct())
                        .build();
                order.addOrderDetail(detail);
            }
            cartRepository.deleteAllInBatch(selectedCarts);

        } else {
            throw new IllegalArgumentException("주문할 상품 정보가 부족합니다.");
        }

        // 배송 정보 설정
        OrderDelivery delivery = OrderDelivery.builder()
                .address(request.getAddress())
                .detail_address(request.getDetailAddress())
                .zipCode(request.getZipCode())
                .build();
        order.setOrderDelivery(delivery);

        return orderRepository.save(order);
    }

    // 주문 내역 목록
    // TODO: 주석 보완 필요
    @Override
    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findAllByUserIdOrderByCreatedDateDesc(userId);

        return orders.stream().map(order -> {
            // 첫 번째 상품 정보를 대표 정보로 사용
            OrderDetail firstItem = order.getOrderDetails().get(0);
            String repName = firstItem.getProduct().getProductName();
            if (order.getOrderDetails().size() > 1) {
                repName += " 외 " + (order.getOrderDetails().size() - 1) + "건";
            }

            return OrderHistoryResponse.builder()
                    .orderId(order.getOrderId())
                    .orderDate(order.getCreatedDate())
                    .totalPrice(order.getTotalPrice())
                    .orderStatus(order.getOrderStatus().name())
                    .representativeProductName(repName)
                    .representativeImageUrl(firstItem.getProduct().getImageUrl())
                    .build();
        }).collect(Collectors.toList());
    }

    // 주문 상세 조회
    // TODO: 주석 보완 필요
    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 테스트용 userId (나중에 Authentication에서 가져오도록 변경)
        Long currentUserId = 1L;

        return OrderDetailResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getCreatedDate())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus().name())
                .address(order.getOrderDelivery().getAddress())
                .detailAddress(order.getOrderDelivery().getDetail_address())
                .zipCode(order.getOrderDelivery().getZipCode())
                .orderItems(order.getOrderDetails().stream().map(item -> {

                    Optional<ProductReview> review = productReviewRepository
                            .findByReviewerIdAndOrderIdAndProductId(currentUserId, orderId, item.getProduct().getId());

                    return OrderDetailResponse.OrderItemDto.builder()
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getProductName())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .imageUrl(item.getProduct().getImageUrl())
                            .reviewId(review.map(ProductReview::getId).orElse(null))
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
}
