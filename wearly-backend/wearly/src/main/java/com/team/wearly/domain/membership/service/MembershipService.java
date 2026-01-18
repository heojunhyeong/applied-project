package com.team.wearly.domain.membership.service;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 멤버십 가입, 상태 변경 및 해지 예약 등 멤버십 생명주기를 관리하는 서비스
 *
 * @author 허준형
 * @DateOfCreated 2026-01-15
 * @DateOfEdit 2026-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipService {
    private final MembershipRepository membershipRepository;

    /**
     * 사용자의 요청에 따라 멤버십을 즉시 종료하지 않고 해지 예약 상태로 전환하는 로직
     *
     * @param userId 해지를 예약하려는 사용자의 식별자
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    public void reserveTermination(Long userId) {
        Membership membership = membershipRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 멤버십이 없습니다."));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("해지 가능한 상태가 아닙니다.");
        }

        // 바로 삭제하는 것이 아니라 '해지 예약' 상태로 변경
        // 이렇게 하면 스케줄러가 이 유저를 결제 대상에서 제외합니다.
        membership.updateStatus(MembershipStatus.CANCELLATION_RESERVED);
        log.info("유저 {} 멤버십 해지 예약 완료", userId);
    }
}