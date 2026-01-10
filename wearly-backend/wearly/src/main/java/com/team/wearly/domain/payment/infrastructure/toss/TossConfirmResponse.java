package com.team.wearly.domain.payment.infrastructure.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 주석 추가 예정
// 토스 API 응답을 받을 DTO
// 우리가 토스 서버에 승인 요청을 보냈을 때, 성공시 데이터를 담는 DTO
@Getter
@NoArgsConstructor
public class TossConfirmResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private Long totalAmount;
    private String method;
}
