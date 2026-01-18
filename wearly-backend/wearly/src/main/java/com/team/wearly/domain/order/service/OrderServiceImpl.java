package com.team.wearly.domain.order.service;

import com.team.wearly.domain.coupon.entity.UserCoupon;
import com.team.wearly.domain.coupon.entity.enums.CouponType;
import com.team.wearly.domain.coupon.repository.UserCouponRepository;
import com.team.wearly.domain.order.dto.response.OrderDetailResponse;
import com.team.wearly.domain.order.dto.response.OrderHistoryResponse;
import com.team.wearly.domain.order.dto.response.OrderSheetResponse;
import com.team.wearly.domain.order.entity.*;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.CartRepository;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.payment.service.SettlementService;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import com.team.wearly.domain.product.entity.enums.Size;
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
     * 새로운 주문을 생성하며, 단일 상품 바로 구매와 장바구니 상품 묶음 구매를 구분하여 처리함
     * 쿠폰 적용 여부에 따라 할인 금액을 계산하고 주문 정보를 저장함
     *
     * @param userId 주문을 수행하는 사용자의 식별자
     * @param request 주문 상품 정보, 배송지 정보, 적용 쿠폰 ID 등을 담은 객체
     * @return 생성된 주문(Order) 엔티티
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2026-01-15
     */
    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId가 null입니다.");
        }

        String orderId = "ORD-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8);

        long serverCalculatedProductPrice = 0L;
        long discountPrice = 0L;
        long deliveryFee = 3000L;

        if (request.getProductId() != null) {

            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            validateProductAvailability(product, request.getSize(), request.getQuantity());

            serverCalculatedProductPrice = product.getPrice() * request.getQuantity();

        } else if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {

            List<Cart> selectedCarts = cartRepository.findAllById(request.getCartItemIds());
            if (selectedCarts.isEmpty()) throw new IllegalArgumentException("장바구니 항목이 존재하지 않습니다.");

            for (Cart cart : selectedCarts) {
                validateProductAvailability(cart.getProduct(), cart.getSize(), cart.getQuantity());
                serverCalculatedProductPrice += cart.getProduct().getPrice() * cart.getQuantity();
            }
        } else {
            throw new IllegalArgumentException("주문할 상품 정보가 부족합니다.");
        }

        if (request.getUserCouponId() != null) {

            UserCoupon targetCoupon = userCouponRepository.findByIdAndUser_Id(request.getUserCouponId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

            if (!targetCoupon.isAvailable(serverCalculatedProductPrice)) {
                throw new IllegalArgumentException("쿠폰 사용 조건이 맞지 않습니다.");
            }

            if (targetCoupon.getCouponType() == CouponType.DISCOUNT_AMOUNT) {
                discountPrice = targetCoupon.getDiscountValue();
            } else if (targetCoupon.getCouponType() == CouponType.DISCOUNT_RATE) {
                discountPrice = (serverCalculatedProductPrice * targetCoupon.getDiscountValue()) / 100;
            }

            targetCoupon.applyToOrder(orderId);
        }

        long finalValidationPrice = serverCalculatedProductPrice - discountPrice + deliveryFee;
        if (finalValidationPrice != request.getTotalPrice()) {
            throw new IllegalArgumentException("결제 요청 금액이 일치하지 않습니다. (요청: " + request.getTotalPrice() + ", 계산: " + finalValidationPrice + ")");
        }

        Order order = Order.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPrice(finalValidationPrice)
                .couponDiscountPrice(discountPrice)
                .orderStatus(OrderStatus.BEFORE_PAID)
                .build();

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId()).orElseThrow();
            order.addOrderDetail(createOrderDetail(product, request.getQuantity(), request.getSize()));
        } else {
            List<Cart> selectedCarts = cartRepository.findAllById(request.getCartItemIds());
            for (Cart cart : selectedCarts) {
                order.addOrderDetail(createOrderDetail(cart.getProduct(), cart.getQuantity(), cart.getSize()));
            }
            cartRepository.deleteAllInBatch(selectedCarts);
        }

        OrderDelivery delivery = OrderDelivery.builder()
                .address(request.getAddress())
                .detail_address(request.getDetailAddress())
                .zipCode(request.getZipCode())
                .build();
        order.setOrderDelivery(delivery);

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSheetResponse getOrderSheet(Long userId, List<Long> cartItemIds, Long productId, Long quantity, Size size) {
        List<OrderSheetResponse.OrderItemDto> items;
        long totalProductPrice;

        // 1. 단품 구매(바로 구매)인 경우
        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            totalProductPrice = product.getPrice() * quantity;
            items = List.of(OrderSheetResponse.OrderItemDto.builder()
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .quantity(quantity)
                    .price(product.getPrice())
                    .size(size.name())
                    .imageUrl(product.getImageUrl())
                    .build());
        }
        // 2. 장바구니 구매인 경우
        else if (cartItemIds != null && !cartItemIds.isEmpty()) {
            List<Cart> selectedCarts = cartRepository.findAllById(cartItemIds);
            if (selectedCarts.isEmpty()) throw new IllegalArgumentException("상품을 선택해주세요.");

            totalProductPrice = selectedCarts.stream()
                    .mapToLong(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                    .sum();

            items = selectedCarts.stream()
                    .map(OrderSheetResponse.OrderItemDto::from)
                    .toList();
        } else {
            throw new IllegalArgumentException("주문 정보가 부족합니다.");
        }

        // 3. 공통: 사용 가능한 쿠폰 조회
        List<OrderSheetResponse.AvailableCouponDto> coupons = userCouponRepository.findByUser_Id(userId).stream()
                .filter(uc -> uc.isAvailable(totalProductPrice))
                .map(uc -> OrderSheetResponse.AvailableCouponDto.builder()
                        .userCouponId(uc.getId())
                        .couponName(uc.getCouponCode())
                        .discountValue(uc.getDiscountValue())
                        .couponType(uc.getCouponType().name())
                        .build())
                .toList();

        return OrderSheetResponse.builder()
                .items(items)
                .totalProductPrice(totalProductPrice)
                .availableCoupons(coupons)
                .deliveryFee(3000L)
                .build();
    }
    /**
     * 사용자의 주문 이력을 최신순으로 조회하며, 여러 상품 주문 시 대표 상품명(외 n건)을 구성함
     *
     * @param userId 주문 내역을 조회할 사용자의 식별자
     * @return 요약된 주문 내역 응답 DTO 리스트
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2026-01-15
     */
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

    /**
     * 특정 주문의 상세 정보를 조회하며, 각 주문 상품별 리뷰 작성 여부를 포함하여 반환함
     *
     * @param orderId 상세 정보를 조회할 주문 번호
     * @return 주문 상세 정보, 배송지 및 개별 상품 목록을 담은 DTO
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2026-01-15
     */
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

    /**
     * 사용자가 배송 완료된 상품의 구매를 확정하고, 해당 품목에 대한 판매자 정산 프로세스를 시작함
     *
     * @param userId 구매 확정을 요청한 사용자의 식별자
     * @param orderId 대상 주문 번호
     * @param orderDetailId 주문 내 개별 상품 상세 번호
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
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

    /**
     * 상품 상태 및 재고 검증 (Size Enum & Long Quantity 적용)
     */
    private void validateProductAvailability(Product product, Size size, Long quantity) {
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("판매 중단된 상품: " + product.getProductName());
        }
        // Product 엔티티의 availableSizes가 List<Size> 형태여야 함
        if (!product.getAvailableSizes().contains(size)) {
            throw new IllegalArgumentException("선택 불가능한 사이즈: " + size);
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("재고 부족: " + product.getProductName());
        }
    }


    /**
     * OrderDetail 생성 (Long Quantity & Size Enum 적용)
     */
    private OrderDetail createOrderDetail(Product product, Long quantity, Size size) {
        return OrderDetail.builder()
                .quantity(quantity)
                .price(product.getPrice())
                .product(product)
                .size(size)
                .sellerId(product.getSellerId())
                .detailStatus(OrderStatus.BEFORE_PAID)
                .build();
    }
}
