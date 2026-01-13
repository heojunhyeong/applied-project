package com.team.wearly.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossBillingConfirmResponse {
    private String billingKey;
    private String customerKey;
    private String method;
    private String cardType;
    private String cardCompany;
    private String authenticatedAt;
}