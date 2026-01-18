package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 주문 상세 DTO
@Getter
@Builder
@AllArgsConstructor
public class OrderDetailResponse {
    private String orderId;
    private LocalDateTime orderDate;
    private Long totalPrice;
    private String orderStatus;

    // 배송 정보
    private String address;
    private String detailAddress;
    private Long zipCode;

    // 주문 상품 상세 리스트
    private List<OrderItemDto> orderItems;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private Brand brand;
        private Long quantity;
        private Long price;
        private String imageUrl;
        private Size size;

        private Long reviewId;

        // 검색 결과에서 주문 정보를 함께 표시하기 위한 필드 (옵셔널)
        private String orderId;
        private LocalDateTime orderDate;
        private Long orderTotalPrice;
        private String orderStatus;
    }
}