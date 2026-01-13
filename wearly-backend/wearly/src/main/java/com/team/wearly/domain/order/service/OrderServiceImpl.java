package com.team.wearly.domain.order.service;

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

    /**
     * 장바구니 주문
     * 주문번호는 현재 날짜와 UUID를 조합하며 생성되며
     * 최초 주문 상태는 {@link OrderStatus#BEFORE_PAID} 로 설정된다
     *
     * 장바구니를 OrderDetail로 변환하고 주문이 완료된 장바구니 목록을 삭제한다
     * TODO: 주석 보완 필요
     * @param request 주문 생성을 위한 요청 객체
     *                - userId: 주문을 생성하는 사용자 ID
     *                - totalPrice: 주문 총 금액
     * @return 생성되어 저장된 Order 엔티티
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2025-01-11
     */
    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateRequest request) {
        // 주문번호 생성
        String orderId = "ORD-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8);


        Order order = Order.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPrice(request.getTotalPrice())
                .orderStatus(OrderStatus.BEFORE_PAID)
                .build();

        // 장바구니에서 선택된 아이템들을 상세 주문으로 변환
        List<Cart> selectedCarts = cartRepository.findAllById(request.getCartItemIds());
        for (Cart cart : selectedCarts) {
            OrderDetail detail = OrderDetail.builder()
                    .quantity(cart.getQuantity())
                    .price(cart.getProduct().getPrice())
                    .product(cart.getProduct())
                    .build();
            order.addOrderDetail(detail);
        }

        OrderDelivery delivery = OrderDelivery.builder()
                .address(request.getAddress())
                .detail_address(request.getDetailAddress())
                .zipCode(request.getZipCode())
                .build();
        order.setOrderDelivery(delivery);

        Order savedOrder = orderRepository.save(order);

        cartRepository.deleteAllInBatch(selectedCarts);

        return savedOrder;
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

                    // [추가된 로직]: 이 상품에 작성된 리뷰가 있는지 확인
                    Optional<ProductReview> review = productReviewRepository
                            .findByReviewerIdAndOrderIdAndProductId(currentUserId, orderId, item.getProduct().getId());

                    return OrderDetailResponse.OrderItemDto.builder()
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getProductName())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .imageUrl(item.getProduct().getImageUrl())
                            // [추가된 필드]: 리뷰가 있으면 ID를, 없으면 null을 반환
                            .reviewId(review.map(ProductReview::getId).orElse(null))
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
}
