package com.team.wearly.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyPageResponse {
    private String userName;
    private String email;

    // 멤버십 관련 정보 요약
    private Boolean isMembershipUser;
    private String membershipStatus;
    private LocalDateTime nextBillingDate;
}