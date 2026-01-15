package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public record SellerOrderListItemResponse(
        String orderId,
        String buyerNickname,        // User.userNickname
        OrderStatus orderStatus,
        Long totalPrice,
        Long itemCount,
        LocalDateTime createdDate
) {}
