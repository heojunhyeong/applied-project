package com.team.wearly.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    PADDING("패딩"),
    SHIRT("셔츠"),
    COAT("코트"),
    HOODIE("후드"),
    SWEATSHIRT("맨투맨"),
    JEANS("청바지"),
    SHORTS("반바지"),
    MUFFLER("머플러");

    private final String title;
}