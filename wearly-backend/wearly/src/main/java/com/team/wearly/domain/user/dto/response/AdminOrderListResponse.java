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
    private String userName;           // 회원 로그인 ID
    private Long totalAmount;          // 총 주문 금액
    private String orderStatus;        // 주문 상태 (BEFORE_PAID, PAID, DELIVERY_COMPLETED, CANCELLED 등)
}
