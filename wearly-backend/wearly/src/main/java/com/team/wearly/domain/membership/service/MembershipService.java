package com.team.wearly.domain.membership.service;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipService {
    private final MembershipRepository membershipRepository;

    public void reserveTermination(Long userId) {
        Membership membership = membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 멤버십이 없습니다."));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("해지 가능한 상태가 아닙니다.");
        }

        // 바로 삭제하는 것이 아니라 '해지 예약' 상태로 변경
        // 이렇게 하면 스케줄러가 이 유저를 결제 대상에서 제외합니다.
        membership.updateStatus(MembershipStatus.CANCEL_RESERVED);
        log.info("유저 {} 멤버십 해지 예약 완료", userId);
    }
}