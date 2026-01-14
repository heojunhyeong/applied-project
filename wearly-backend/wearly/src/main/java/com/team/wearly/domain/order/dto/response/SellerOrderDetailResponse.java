package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.enums.Carrier;
import com.team.wearly.domain.order.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SellerOrderDetailResponse(
        String orderId,
        OrderStatus orderStatus,

        String userId,          // 구매자 로그인 아이디 (User.userName)
        String userName,        // 구매자 닉네임 (User.userNickname)

        Long totalPrice,
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
            Long productId,
            String productName,
            String imageUrl,
            Long quantity,
            Long price
    ) {}
}
