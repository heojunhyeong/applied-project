package com.team.wearly.domain.payment.entity.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CARD,
    TRANSFER,
    VIRTUAL_ACCOUNT,
    MOBILE_PHONE;
}