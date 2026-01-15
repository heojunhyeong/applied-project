package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.enums.Carrier;
import com.team.wearly.domain.order.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SellerOrderDetailResponse(
        String orderId,
        OrderStatus orderStatus,     // 셀러 기준 상태 (detailStatus)
        String buyerLoginId,         // User.userName
        String buyerNickname,        // User.userNickname
        Long totalPrice,             // 셀러 기준 합계로 넣는 걸 추천
        LocalDateTime createdDate,
        Delivery delivery,
        List<Item> items
) {
    public record Delivery(
            String address,
            String detailAddress,
            Long zipCode,
            Carrier carrier,
            String invoiceNumber
    ) {}

    public record Item(
            Long orderDetailId,
            Long productId,
            String productName,
            String imageUrl,
            Long quantity,
            Long price,
            OrderStatus detailStatus
    ) {}
}
