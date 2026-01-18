package com.team.wearly.domain.membership.dto;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MembershipResponse {
    private MembershipStatus status;
    private LocalDateTime nextPaymentDate;

    public static MembershipResponse from(Membership membership) {
        return MembershipResponse.builder()
                .status(membership.getStatus())
                .nextPaymentDate(membership.getNextPaymentDate())
                .build();
    }
}