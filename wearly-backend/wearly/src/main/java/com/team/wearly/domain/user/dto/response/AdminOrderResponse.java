package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.payment.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdminOrderResponse {
    private Long orderId;              // 주문 ID
    private String orderNumber;        // 주문 번호 (orderId)
    private Long userId;               // 회원 ID
    private String userNickname;       // 회원 닉네임
    private String userEmail;          // 회원 이메일
    private LocalDateTime orderDate;   // 주문 일시
    private Long totalPrice;           // 총 주문 금액
    private Long couponDiscountPrice;  // 쿠폰 할인 금액
    private Long finalPrice;           // 최종 결제 금액
    private String orderStatus;        // 주문 상태 (BEFORE_PAID, PAID, IN_DELIVERY, DELIVERY_COMPLETED 등)
    private String deliveryStatus;     // 배송 상태 (주문 상태 기반)
    private Boolean isPaid;            // 결제 완료 여부
    private PaymentInfo paymentInfo;    // 결제 정보
    private List<OrderItemInfo> orderItems; // 주문 상품 목록
    private DeliveryInfo deliveryInfo;  // 배송 정보

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaymentInfo {
        private Boolean exists;         // 결제 내역 존재 여부
        private PaymentStatus status;   // 결제 상태 (DONE, CANCELED 등)
        private Long amount;           // 결제 금액
        private String paymentMethod;  // 결제 수단
        private LocalDateTime paymentDate; // 결제 일시
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class OrderItemInfo {
        private Long productId;
        private String productName;
        private String imageUrl;
        private Long quantity;
        private Long price;
        private Long totalItemPrice;   // 상품 가격 * 수량
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DeliveryInfo {
        private String address;
        private String detailAddress;
        private Long zipCode;
        private String carrier;        // 택배사
        private String invoiceNumber;  // 송장번호
    }
}
