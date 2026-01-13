package com.team.wearly.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * 토스 결제창에서 인증을 마치고 나면 프론트엔드(React)에서 우리 서버로 결제 승인 요청을 보낼 때 사용하는 DTO
 * 즉, 토스(React) -> 서버(Spring)
 *
 * @author 허준형
 * @DateOfCreated 2026-01-10
 * @DateOfEdit 2025-01-10
 */
@Getter
@Setter
@AllArgsConstructor
public class TossPaymentConfirmRequest {

    // 토스에서 발급한 고유 결제 키
    private String paymentKey;

    // 우리가 만든 주문 번호
    private String orderId;

    private Long amount;
}
