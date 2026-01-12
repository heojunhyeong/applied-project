package com.team.wearly.domain.review.entity.enums;

//관리자 파트 관련
public enum ReviewReportStatus {
    PENDING,      // 신고 접수됨, 아직 관리자 판단 전
    RESOLVED,     // 관리자 "승인"처리 -> ReviewStatus : Hidden 변경
    REJECTED      // 관리자 "반려"처리 -> ReviewStatus : Active 변경
}
