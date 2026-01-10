package com.team.wearly.domain.payment.entity.enums;

public enum PaymentStatus {
    READY,     // 결제 생성됨 (인증 전)
    DONE,      // 결제 완료 (승인 성공)
    CANCELED,  // 결제 취소됨
    EXPIRED,   // 결제 시간 만료
    ABORTED    // 결제 승인 실패
}