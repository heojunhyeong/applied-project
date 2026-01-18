package com.team.wearly.domain.payment.infrastructure.toss;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 우리가 토스 서버에 승인 요청을 보냈을 때 성공 시 토스가 보내주는 데이터를 담을 DTO
 *
 * @author 허준형
 * @DateOfCreated 2026-01-10
 * @DateOfEdit 2025-01-10
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossConfirmResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private Long totalAmount;
    private String method;
    private String approvedAt;
}
