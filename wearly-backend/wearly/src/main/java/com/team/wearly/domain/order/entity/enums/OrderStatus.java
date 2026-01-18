package com.team.wearly.domain.order.entity.enums;

public enum OrderStatus {
    BEFORE_PAID,
    PAID,
    WAIT_CHECK,
    CHECK,
    IN_DELIVERY,
    DELIVERY_COMPLETED,

    CANCELLED,

    PURCHASE_CONFIRMED,

    //반품 처리를 위한 항목
    RETURN_REQUESTED,
    RETURN_COMPLETED,
}
