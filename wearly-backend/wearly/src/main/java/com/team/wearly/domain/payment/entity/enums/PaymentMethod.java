package com.team.wearly.domain.payment.entity.enums;

import lombok.Getter;

// 사실 description 굳이 필요없는데 복습겸 써봤음
// TODO: 해당 주석 지워야함 ㅎ;
@Getter
public enum PaymentMethod {
    CARD("카드"),
    TRANSFER("계좌이체"),
    VIRTUAL_ACCOUNT("가상계좌"),
    MOBILE_PHONE("휴대폰");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }
}