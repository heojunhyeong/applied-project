package com.team.wearly.domain.order.service;

import com.team.wearly.domain.coupon.entity.UserCoupon;
import com.team.wearly.domain.coupon.entity.enums.CouponType;
import com.team.wearly.domain.coupon.repository.UserCouponRepository;
import com.team.wearly.domain.order.dto.response.OrderDetailResponse;
import com.team.wearly.domain.order.dto.response.OrderHistoryResponse;
import com.team.wearly.domain.order.entity.*;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.CartRepository;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.payment.service.SettlementService;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
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
    private final UserCouponRepository userCouponRepository;
    private final SettlementService settlementService;

    /**
     * 주문 로직
     * 주문번호는 현재 날짜와 UUID를 조합하며 생성되며
     * 최초 주문 상태는 {@link OrderStatus#BEFORE_PAID} 로 설정된다
     * 단일 주문 상품 정보(사이트에서 주문하기 클릭)시 바로 구매로 우선 처리하고
     * 단일 주문 상품 정보가 없을 시 장바구니 구매로 처리한다
     * <p>
     * TODO: 주석 보완 필요
     *
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

            // 삭제된 상품 주문 방지
            if (product.getStatus() == ProductStatus.DELETED) {
                throw new IllegalArgumentException("판매가 중단된 상품입니다.");
            }

            // 사용자가 요청한 사이즈가 해당 상품의 판매 가능 목록에 없으면 예외 발생 추가
            if (!product.getAvailableSizes().contains(request.getSize())) {
                throw new IllegalArgumentException("해당 상품에서 선택할 수 없는 사이즈입니다: " + request.getSize());
            }

            if (product.getStockQuantity() < request.getQuantity()) {
                throw new IllegalStateException("상품 재고가 부족합니다.");
            }

            OrderDetail detail = OrderDetail.builder()
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .product(product)
                    .size(request.getSize())
                    .sellerId(product.getSellerId())
                    .build();
            order.addOrderDetail(detail);

        } else if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            List<Cart> selectedCarts = cartRepository.findAllById(request.getCartItemIds());

            if (selectedCarts.isEmpty()) {
                throw new IllegalArgumentException("장바구니 항목이 존재하지 않습니다.");
            }

            for (Cart cart : selectedCarts) {

                // 장바구니 상품 중 삭제된 게 있는지 확인
                if (cart.getProduct().getStatus() == ProductStatus.DELETED) {
                    throw new IllegalArgumentException("장바구니에 판매 중단된 상품이 포함되어 있습니다: " + cart.getProduct().getProductName());
                }

                OrderDetail detail = OrderDetail.builder()
                        .quantity(cart.getQuantity())
                        .price(cart.getProduct().getPrice())
                        .product(cart.getProduct())
                        .sellerId(cart.getProduct().getSellerId())
                        .size(cart.getSize())
                        .detailStatus(OrderStatus.BEFORE_PAID) // 초기 상태 명시
                        .build();

                OrderDeliveryDetail deliveryDetail = OrderDeliveryDetail.builder()
                        .orderDetail(detail)
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
                            .size(item.getSize())
                            .reviewId(review.map(ProductReview::getId).orElse(null))
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void confirmPurchase(Long userId, String orderId, Long orderDetailId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getUserId().equals(userId)) {
            throw new SecurityException("본인의 주문만 구매 확정할 수 있습니다.");
        }

        OrderDetail targetDetail = order.getOrderDetails().stream()
                .filter(d -> d.getId().equals(orderDetailId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 상품 상세 내역이 없습니다."));

        if (targetDetail.getDetailStatus() != OrderStatus.DELIVERY_COMPLETED) {
            throw new IllegalStateException("배송 완료된 상품만 구매 확정이 가능합니다.");
        }

        targetDetail.updateDetailStatus(OrderStatus.PURCHASE_CONFIRMED);

        settlementService.markItemAsSettlementTarget(
                orderId,                       // String
                targetDetail.getSellerId(),    // Long
                targetDetail.getProduct().getId() // Long
        );

        boolean allConfirmed = order.getOrderDetails().stream()
                .allMatch(d -> d.getDetailStatus() == OrderStatus.PURCHASE_CONFIRMED);
        if (allConfirmed) {
            order.updateStatus(OrderStatus.PURCHASE_CONFIRMED);
        }
    }

    /**
     * 상품명 키워드로 주문 상세 검색 (같은 날 주문된 상품 포함)
     * @param userId 사용자 ID (본인의 주문만 검색)
     * @param keyword 상품명 검색 키워드
     * @return 키워드가 포함된 상품과 같은 날 주문된 모든 상품의 주문 상세 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponse.OrderItemDto> searchOrderDetailsByKeyword(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        List<OrderDetail> orderDetails = orderRepository.findOrderDetailsByKeywordWithSameDateOrders(keyword, userId);

        return orderDetails.stream()    // 리스트를 자바 스트림으로 변환
                .map(detail -> OrderDetailResponse.OrderItemDto.builder()
                        .productId(detail.getProduct().getId())
                        .productName(detail.getProduct().getProductName())
                        .quantity(detail.getQuantity())
                        .price(detail.getPrice())
                        .imageUrl(detail.getProduct().getImageUrl())
                        .size(detail.getSize())
                        .reviewId(null) // 검색 결과에는 리뷰 ID는 필요 없을 수도 있음
                        .build())
                .distinct() // 중복 제거
                .collect(Collectors.toList());
    }
}
