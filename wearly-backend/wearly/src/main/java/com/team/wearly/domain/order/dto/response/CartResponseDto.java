package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.Cart;
import com.team.wearly.domain.product.entity.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    private Long cartId;
    private Long productId;
    private String productName;
    private Long price;
    private Long quantity;
    private Size size;
    private String imageUrl;

    public static CartResponseDto from(Cart cart) {
        return CartResponseDto.builder()
                .cartId(cart.getId())
                .productId(cart.getProduct().getId())
                .productName(cart.getProduct().getProductName())
                .price(cart.getProduct().getPrice())
                .quantity(cart.getQuantity())
                .imageUrl(cart.getProduct().getImageUrl())
                .build();
    }
}