package com.team.wearly.domain.review.entity.enums;

public enum ReviewStatus {
    ACTIVE,        // 정상 노출(신고거절되면 다시 ACTIVE로)
    HIDDEN         // 숨김(관리자/정책 처리)
}
