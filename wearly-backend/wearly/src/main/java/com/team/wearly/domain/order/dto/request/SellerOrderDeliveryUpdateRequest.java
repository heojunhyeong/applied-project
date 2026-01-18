package com.team.wearly.domain.order.dto.request;

import com.team.wearly.domain.order.entity.enums.Carrier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SellerOrderDeliveryUpdateRequest(
        // 구매자가 입력한 배송지 정보는 셀러가 함부로 변경 할 수 없음
        // 필수 구현
        // check -> IN_DELIVERY 로 넘길때 택배사와 송장번호 입력 값 필수
        @NotNull Carrier carrier,
        @NotBlank String invoiceNumber
) {}
