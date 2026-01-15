package com.team.wearly.domain.product.dto.request;

public enum ProductSortType {
    LATEST("최신순"),
    PRICE_LOW("낮은 가격순"),
    PRICE_HIGH("높은 가격순");

    private final String description;

    ProductSortType(String description) {
        this.description = description;
    }
}