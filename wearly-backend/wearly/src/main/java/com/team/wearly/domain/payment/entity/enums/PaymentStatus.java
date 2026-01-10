package com.team.wearly.domain.payment.entity.enums;


import lombok.Getter;

/**
 * 현재 결제 상태가 어떤지 나타내는 열거형 클래스
 *
 * @author 허준형
 * @DateOfCreated 2026-01-10
 * @DateOfEdit 2025-01-10
 */
@Getter
public enum PaymentStatus {
    READY,     // 결제 생성됨 (인증 전)
    DONE,      // 결제 완료 (승인 성공)
    CANCELED,  // 결제 취소됨
    EXPIRED,   // 결제 시간 만료
    ABORTED    // 결제 승인 실패
}