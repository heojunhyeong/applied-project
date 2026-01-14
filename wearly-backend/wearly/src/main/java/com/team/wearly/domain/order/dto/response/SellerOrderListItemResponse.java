package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public record SellerOrderListItemResponse(
        String orderId,
        String userName,        // 구매자 닉네임 (User.userNickname)
        OrderStatus orderStatus,
        Long totalPrice,
        Long itemCount,
        LocalDateTime createdDate
) {}
