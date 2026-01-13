package com.team.wearly.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Brand {
    NIKE("나이키"),
    ADIDAS("아디다스"),
    NEW_BALANCE("뉴발란스"),
    REEBOK("리복"),
    THE_NORTH_FACE("노스페이스"),
    VANS("반스");

    private final String title;
}