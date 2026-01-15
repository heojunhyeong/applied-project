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
import com.team.wearly.domain.user.entity.User;
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

    /**
     * 관리자용 주문 내역 조회 (검색 기능 포함) - 간단한 정보만 반환
     * @param nickname User 닉네임 (검색어, null이면 전체 조회)
     * @return 주문 내역 리스트 (주문id, 주문번호, 회원id, 결제내역 O/X)
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

        return orders.stream()
                .map(this::convertToAdminOrderListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 관리자용 주문 상세 조회
     * @param orderId 주문 ID
     * @return 주문 상세 정보
     */
    public AdminOrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
        
        return convertToAdminOrderResponse(order);
    }

    /**
     * Order 엔티티를 AdminOrderListResponse로 변환 (간단한 정보만)
     */
    private AdminOrderListResponse convertToAdminOrderListResponse(Order order) {
        // 결제 정보 조회
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(order.getOrderId());
        boolean isPaid = paymentOpt.isPresent() && paymentOpt.get().getStatus() == PaymentStatus.DONE;
        String paymentStatus = isPaid ? "O" : "X";

        return AdminOrderListResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderId())
                .userId(order.getUserId())
                .paymentStatus(paymentStatus)
                .build();
    }

    /**
     * Order 엔티티를 AdminOrderResponse로 변환
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
     * 결제 정보 구성
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
     * 배송 상태 결정 (검수 부분 제거)
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
     * OrderDetail을 OrderItemInfo로 변환
     */
    private AdminOrderResponse.OrderItemInfo convertToOrderItemInfo(OrderDetail detail) {
        return AdminOrderResponse.OrderItemInfo.builder()
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getProductName())
                .imageUrl(detail.getProduct().getImageUrl())
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .totalItemPrice(detail.getPrice() * detail.getQuantity())
                .build();
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
