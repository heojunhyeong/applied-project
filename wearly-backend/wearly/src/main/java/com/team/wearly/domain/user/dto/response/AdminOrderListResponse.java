package com.team.wearly.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderListResponse {
    private Long orderId;              // 주문 ID
    private String orderNumber;        // 주문 번호
    private Long userId;               // 회원 ID
    private String paymentStatus;      // 결제 내역 (O 또는 X)
}
