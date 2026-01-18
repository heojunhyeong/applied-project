package com.team.wearly.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {

    // 1. 정상 판매 중 (목록 노출 O, 구매 O)
    ON_SALE("판매중"),

    // 2. 재고 없음 (목록 노출 O, 구매 X)
    // - 시스템이 재고 0일 때 자동으로 바꾸거나,
    // - 판매자가 잠시 판매를 멈추고 싶을 때 수동으로 선택
    SOLD_OUT("품절"),

    // 3. 삭제됨 (목록 노출 X)
    // - 판매자가 상품을 삭제했거나, 관리자가 내린 경우
    // - Soft Delete 개념으로 DB에는 남지만 사용자 눈에는 안 보임
    DELETED("삭제됨");

    private final String title;
}