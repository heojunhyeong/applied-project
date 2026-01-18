package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderSheetResponse {
    private List<OrderItemDto> items;
    private Long totalProductPrice;
    private List<AvailableCouponDto> availableCoupons;
    private Long deliveryFee; // 필요시 추가 (예: 3000원)

    @Getter
    @Builder
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private Long quantity;
        private Long price;
        private String size; // Enum일 경우 .name() 또는 String
        private String imageUrl;

        public static OrderItemDto from(Cart cart) {
            return OrderItemDto.builder()
                    .productId(cart.getProduct().getId())
                    .productName(cart.getProduct().getProductName())
                    .quantity(cart.getQuantity())
                    .price(cart.getProduct().getPrice())
                    .size(cart.getSize().name())
                    .imageUrl(cart.getProduct().getImageUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AvailableCouponDto {
        private Long userCouponId;
        private String couponName;
        private Long discountValue;
        private String couponType; // DISCOUNT_AMOUNT, DISCOUNT_RATE
    }
}