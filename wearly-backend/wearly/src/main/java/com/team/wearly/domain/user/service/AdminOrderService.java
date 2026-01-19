package com.team.wearly.domain.user.service;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.OrderDelivery;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.OrderRepository;
import com.team.wearly.domain.payment.entity.Payment;
import com.team.wearly.domain.payment.entity.enums.PaymentStatus;
import com.team.wearly.domain.payment.repository.PaymentRepository;
import com.team.wearly.domain.user.dto.response.AdminOrderListResponse;
import com.team.wearly.domain.user.dto.response.AdminOrderResponse;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    /**
     * 관리자용 주문 목록을 조회함 (회원 닉네임 검색 지원)
     *
     * @param nickname 검색할 사용자의 닉네임 (null일 경우 전체 조회)
     * @return 요약된 주문 정보 리스트 (주문ID, 주문번호, 회원ID, 결제 상태 포함)
     * @author 최윤혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    public List<AdminOrderListResponse> getOrders(String nickname) {
        List<Order> orders;

        if (nickname != null && !nickname.isBlank()) {
            // 닉네임으로 검색
            orders = orderRepository.findByUserNickname(nickname);
        } else {
            // 전체 조회 (최신순)
            orders = orderRepository.findAllByOrderByCreatedDateDesc();
        }

        // 결제 완료(DONE) 또는 결제 실패(ABORTED, EXPIRED)된 주문만 필터링
        return orders.stream()
                .filter(order -> {
                    Optional<Payment> paymentOpt = paymentRepository.findByOrderId(order.getOrderId());
                    if (paymentOpt.isEmpty()) {
                        return false;
                    }
                    PaymentStatus status = paymentOpt.get().getStatus();
                    return status == PaymentStatus.DONE || status == PaymentStatus.ABORTED || status == PaymentStatus.EXPIRED;
                })
                .map(this::convertToAdminOrderListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 주문의 상세 정보(구매자 정보, 결제 상세, 주문 상품 목록 등)를 종합하여 조회함
     *
     * @param orderId 주문 식별자 (PK)
     * @return 관리자용 주문 상세 응답 DTO
     * @author 최윤혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    public AdminOrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
        
        return convertToAdminOrderResponse(order);
    }

    /**
     * Order 엔티티를 관리자 목록용 응답 DTO로 변환하며 실제 결제 완료 여부를 판단함
     *
     * @param order 주문 엔티티
     * @return 목록용 응답 DTO
     * @author 최윤혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    private AdminOrderListResponse convertToAdminOrderListResponse(Order order) {
        // User 정보 조회
        User user = userRepository.findById(order.getUserId())
                .orElse(null);

        // 총 주문 금액 계산 (쿠폰 할인 포함)
        Long totalAmount = order.getTotalPrice() - (order.getCouponDiscountPrice() != null ? order.getCouponDiscountPrice() : 0L);

        return AdminOrderListResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderId())
                .userId(order.getUserId())
                .userName(user != null ? user.getUserName() : null)
                .totalAmount(totalAmount)
                .orderStatus(order.getOrderStatus().name())
                .build();
    }

    /**
     * 여러 도메인(회원, 결제, 주문 상세)의 데이터를 결합하여 상세 응답 DTO를 구성함
     *
     * @param order 주문 엔티티
     * @author 최윤혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    private AdminOrderResponse convertToAdminOrderResponse(Order order) {
        // User 정보 조회
        User user = userRepository.findById(order.getUserId())
                .orElse(null);

        // 결제 정보 조회
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(order.getOrderId());
        AdminOrderResponse.PaymentInfo paymentInfo = buildPaymentInfo(paymentOpt);

        // 배송 상태 결정
        String deliveryStatus = determineDeliveryStatus(order.getOrderStatus());

        // 주문 상품 목록
        List<AdminOrderResponse.OrderItemInfo> orderItems = order.getOrderDetails().stream()
                .map(this::convertToOrderItemInfo)
                .collect(Collectors.toList());

        // 배송 정보
        //AdminOrderResponse.DeliveryInfo deliveryInfo = buildDeliveryInfo(order.getOrderDelivery(), order.getOrderDetails());

        return AdminOrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderId())
                .userId(user != null ? user.getId() : null)
                .userName(user != null ? user.getUserName() : null)
                .userNickname(user != null ? user.getUserNickname() : "알 수 없음")
                .userEmail(user != null ? user.getUserEmail() : "알 수 없음")
                .orderDate(order.getCreatedDate())
                .totalPrice(order.getTotalPrice())
                .couponDiscountPrice(order.getCouponDiscountPrice())
                .finalPrice(order.getTotalPrice() - (order.getCouponDiscountPrice() != null ? order.getCouponDiscountPrice() : 0L))
                .orderStatus(order.getOrderStatus().name())
                .deliveryStatus(deliveryStatus)
                .isPaid(paymentInfo.getExists() && paymentInfo.getStatus() == PaymentStatus.DONE)
                .paymentInfo(paymentInfo)
                .orderItems(orderItems)
                //.deliveryInfo(deliveryInfo)
                .build();
    }

    /**
     * 결제 내역 존재 여부에 따라 관리자용 결제 정보 컴포넌트를 생성함
     *
     * @author 최윤혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    private AdminOrderResponse.PaymentInfo buildPaymentInfo(Optional<Payment> paymentOpt) {
        if (paymentOpt.isEmpty()) {
            return AdminOrderResponse.PaymentInfo.builder()
                    .exists(false)
                    .status(null)
                    .amount(null)
                    .paymentMethod(null)
                    .paymentDate(null)
                    .build();
        }

        Payment payment = paymentOpt.get();
        return AdminOrderResponse.PaymentInfo.builder()
                .exists(true)
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .paymentMethod(payment.getMethod() != null ? payment.getMethod().name() : null)
                .paymentDate(payment.getCreatedDate())
                .build();
    }

    /**
     * 내부 도메인 상태(OrderStatus)를 관리자 웹 화면에 표시할 명칭으로 매핑함
     *
     * @author 최윤혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    private String determineDeliveryStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case BEFORE_PAID -> "결제 전";
            case PAID -> "결제 완료";
            case IN_DELIVERY -> "배송중";
            case DELIVERY_COMPLETED -> "배송 완료";
            case CANCELLED -> "취소됨";
            case RETURN_REQUESTED -> "반품 요청";
            case RETURN_COMPLETED -> "반품 완료";
            default -> orderStatus.name(); // WAIT_CHECK, CHECK 등은 그대로 표시
        };
    }

    /**
     * 주문 상세 엔티티를 상품 정보가 포함된 DTO로 변환함
     *
     * @author 최윤혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    private AdminOrderResponse.OrderItemInfo convertToOrderItemInfo(OrderDetail detail) {
        Long sellerId = detail.getProduct().getSellerId();
        String sellerName = null;

        // Seller 정보 조회 (userName을 위해)
        if (sellerId != null) {
            Optional<Seller> seller = sellerRepository.findById(sellerId);
            sellerName = seller.map(Seller::getUserName).orElse(null);
        }

        return AdminOrderResponse.OrderItemInfo.builder()
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getProductName())
                .imageUrl(detail.getProduct().getImageUrl())
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .totalItemPrice(detail.getPrice() * detail.getQuantity())
                .sellerId(sellerId)
                .sellerName(sellerName)
                .build();
    }

    /**
     * 관리자 권한으로 주문을 취소함
     * Pending 상태(BEFORE_PAID)인 주문만 취소 가능
     *
     * @param orderId 취소할 주문의 식별자
     * @throws IllegalArgumentException 주문을 찾을 수 없거나 취소할 수 없는 상태일 경우 발생
     * @author 최윤혁
     * @DateOfCreated 2026-01-19
     * @DateOfEdit 2026-01-19
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
        
        if (order.getOrderStatus() != OrderStatus.BEFORE_PAID) {
            throw new IllegalStateException("Pending 상태의 주문만 취소할 수 있습니다.");
        }
        
        order.updateStatus(OrderStatus.CANCELLED);
    }

    /**
     * 관리자 권한으로 주문을 삭제함
     * 주문과 연관된 OrderDetail, OrderDelivery 등도 cascade로 함께 삭제됨
     *
     * @param orderId 삭제할 주문의 식별자
     * @throws IllegalArgumentException 주문을 찾을 수 없을 경우 발생
     * @author 최윤혁
     * @DateOfCreated 2026-01-19
     * @DateOfEdit 2026-01-19
     */
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
        
        orderRepository.delete(order);
    }

//    /**
//     * 배송 정보 구성
//     */
//    private AdminOrderResponse.DeliveryInfo buildDeliveryInfo(OrderDelivery delivery, List<OrderDetail> orderDetails) {
//        if (delivery == null) {
//            return AdminOrderResponse.DeliveryInfo.builder()
//                    .address(null)
//                    .detailAddress(null)
//                    .zipCode(null)
//                    .carrier(null)
//                    .invoiceNumber(null)
//                    .build();
//        }
//
//        // 첫 번째 OrderDetail의 OrderDeliveryDetail에서 carrier와 invoiceNumber 가져오기
//        String carrier = null;
//        String invoiceNumber = null;
//        
//        if (orderDetails != null && !orderDetails.isEmpty()) {
//            OrderDetail firstDetail = orderDetails.get(0);
//            if (firstDetail.getDeliveryDetail() != null) {
//                OrderDeliveryDetail deliveryDetail = firstDetail.getDeliveryDetail();
//                carrier = deliveryDetail.getCarrier() != null ? deliveryDetail.getCarrier().name() : null;
//                invoiceNumber = deliveryDetail.getInvoiceNumber();
//            }
//        }
//
//        return AdminOrderResponse.DeliveryInfo.builder()
//                .address(delivery.getAddress())
//                .detailAddress(delivery.getDetail_address())
//                .zipCode(delivery.getZipCode())
//                .carrier(carrier)
//                .invoiceNumber(invoiceNumber)
//                .build();
//    }
}
