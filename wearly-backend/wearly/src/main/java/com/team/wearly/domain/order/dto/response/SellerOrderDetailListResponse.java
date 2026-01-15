package com.team.wearly.domain.order.dto.response;

import com.team.wearly.domain.order.entity.enums.Carrier;
import com.team.wearly.domain.order.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public record SellerOrderDetailListResponse(

        Long orderDetailId,
        String orderId,                // 참조용 주문번호

        String buyerName,              // User.userName
        String buyerNickname,          // User.userNickname

        OrderStatus orderStatus,       // Order.orderStatus (결제 상태)
        OrderStatus detailStatus,      // 판매자 진행 상태

        Long productId,
        String productName,
        String productImageUrl,

        Long quantity,
        Long price,

        Carrier carrier,
        String invoiceNumber,

        LocalDateTime orderedAt
) {}
