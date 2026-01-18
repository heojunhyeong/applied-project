package com.team.wearly.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BillingConfirmRequest {
    private String authKey;      // 토스에서 받은 임시 인증 키
    private String customerKey;  // 프론트에서 생성해서 보낸 유저 식별 키
}