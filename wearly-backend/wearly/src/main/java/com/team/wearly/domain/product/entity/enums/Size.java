package com.team.wearly.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Size {
    // (전체 이름, 약어) 순서
    SMALL("Small", "S"),
    MEDIUM("Medium", "M"),
    LARGE("Large", "L"),
    EXTRA_LARGE("Extra Large", "XL");

    private final String description;
    private final String abbreviation;
}