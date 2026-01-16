package com.team.wearly.domain.payment.batch;

import com.team.wearly.domain.payment.entity.Settlement;
import com.team.wearly.domain.payment.entity.enums.SettlementStatus;
import com.team.wearly.domain.payment.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 확정된 정산 데이터를 기반으로 판매자에게 실제 대금을 지급하는 배치 스케줄러
 *
 * @author 허준형
 * @DateOfCreated 2026-01-15
 * @DateOfEdit 2026-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementBatchScheduler {

    private final SettlementRepository settlementRepository;

    /**
     * 매달 10일 정산 확정 상태의 내역을 조회하여 판매자 계좌로 입금 처리를 수행함
     *
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    // 매달 10일 새벽 1시에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 1 10 * *")
    @Transactional
    public void runMonthlySettlement() {
        log.info("정산 지급 배치 시작...");

        // 지급 대상 조회
        List<Settlement> targets = settlementRepository.findAllByStatus(SettlementStatus.CONFIRMED);

        for (Settlement s : targets) {
            try {
                // 실제 은행 API 호출 로직 (가정)
                // bankingService.transfer(s.getSellerAccount(), s.getSettlementAmount());

                s.completeSettlement();
            } catch (Exception e) {
                log.error("판매자 {} 정산 지급 실패: {}", s.getSellerId(), e.getMessage());
            }
        }

        log.info("정산 지급 배치 완료: {}건 처리", targets.size());
    }
}