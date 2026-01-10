package com.team.wearly.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


// 주석 추가 예정
// 프론트에서 보내줄 DTO
// 토스 결제창에서 인증을 마치고 난 뒤 프론트에서 보내는 DTO
@Getter
@Setter
@AllArgsConstructor
public class TossPaymentConfirmRequest {

    // 토스에서 발급한 고유 결제 키
    private String paymentKey;

    // 우리가 만든 주문 번호
    private String orderId;

    // 총 결제 금액
    private Long amount;
}
