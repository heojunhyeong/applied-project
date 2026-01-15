package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.enums.Carrier;
import com.team.wearly.domain.order.entity.enums.OrderStatus;

import java.time.LocalDateTime;
// orderDetail 단건
public record SellerOrderDetailResponse(

        Long orderDetailId,
        String orderId,

        String buyerName,
        String buyerNickname,

        OrderStatus orderStatus,
        OrderStatus detailStatus,

        ProductInfo product,
        DeliveryAddress deliveryAddress,

        Carrier carrier,
        String invoiceNumber,

        Long quantity,
        Long price,

        LocalDateTime orderedAt
) {

    public record ProductInfo(
            Long productId,
            String productName,
            String imageUrl
    ) {}

    // 구매자 입력 배송지 (수정 불가)
    public record DeliveryAddress(
            String address,
            String detailAddress,
            Long zipCode
    ) {}
}
