package com.team.wearly.domain.membership.batch;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import com.team.wearly.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipScheduler {

    private final MembershipRepository membershipRepository;
    private final PaymentService paymentService;

    // 매일 새벽 0시 0분 0초에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runMonthlyPayment() {
        log.info("정기 결제 배치 작업 시작: {}", LocalDateTime.now());

        // 오늘 결제 대상자 조회 (현재 시간 이전이 결제일인 모든 ACTIVE 유저)
        List<Membership> targets = membershipRepository.findAllByStatusAndNextPaymentDateBefore(
                MembershipStatus.ACTIVE, LocalDateTime.now());

        log.info("결제 대상자 수: {}명", targets.size());

        for (Membership membership : targets) {
            try {
                paymentService.executeScheduledPayment(membership);
            } catch (Exception e) {
                log.error("유저 {} 정기 결제 중 예외 발생", membership.getUserId());
            }
        }

        log.info("정기 결제 배치 작업 종료");
    }

    // 해지 예약자 중 기간이 끝난 사람을 찾아 완전 종료 처리
    // 매일 새벽 12시 30분에 실행
    @Scheduled(cron = "0 30 0 * * *")
    public void processTerminations() {
        List<Membership> reservedUsers = membershipRepository.findAllByStatusAndNextPaymentDateBefore(
                MembershipStatus.CANCEL_RESERVED, LocalDateTime.now());

        for (Membership m : reservedUsers) {
            m.updateStatus(MembershipStatus.INACTIVE);
            log.info("유저 {} 멤버십 이용기간 만료 및 완전 종료", m.getUserId());
        }
    }
}