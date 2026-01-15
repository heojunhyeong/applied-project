package com.team.wearly.domain.product.dto.request;

import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;

public record ProductSearchCondition(
        Brand brand,              // 브랜드 필터
        ProductCategory category, // 카테고리 필터
        String keyword,           // 검색어
        ProductSortType sort      // 정렬 조건 추가
) {}