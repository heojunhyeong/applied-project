package com.team.wearly.domain.order.dto.request;

import com.team.wearly.domain.order.entity.enums.Carrier;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record SellerOrderDetailStatusUpdateRequest(
        @NotNull OrderStatus nextStatus,
        Carrier carrier,
        String invoiceNumber
) {}
