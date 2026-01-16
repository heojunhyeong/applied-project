package com.team.wearly.domain.payment.controller;

import com.team.wearly.domain.payment.entity.enums.SettlementStatus;
import com.team.wearly.domain.payment.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 판매자별 정산 예정 금액 및 정산 완료 금액 등 통계 정보를 제공하는 컨트롤러
 *
 * @author 허준형
 * @DateOfCreated 2026-01-15
 * @DateOfEdit 2026-01-15
 */
@RestController
@RequestMapping("/api/seller/settlements")
@RequiredArgsConstructor
public class SellerSettlementController {

    private final SettlementRepository settlementRepository;

    /**
     * 판매자의 정산 상태별 금액을 합산하여 정산 예정 금액과 정산 완료 총액을 반환하는 API
     *
     * @param authentication 인증된 판매자의 정보 (sellerId 추출용)
     * @return 정산 예정 금액(expectAmount)과 정산 완료 금액(completedAmount)을 담은 Map
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSummary(Authentication authentication) {
        Long sellerId = Long.parseLong(authentication.getName());

        // null 방지를 위해 0 처리
        Long ready = settlementRepository.sumAmountBySellerAndStatus(sellerId, SettlementStatus.READY);
        Long confirmed = settlementRepository.sumAmountBySellerAndStatus(sellerId, SettlementStatus.CONFIRMED);
        Long completed = settlementRepository.sumAmountBySellerAndStatus(sellerId, SettlementStatus.COMPLETED);

        return ResponseEntity.ok(Map.of(
                "expectAmount", (ready != null ? ready : 0L) + (confirmed != null ? confirmed : 0L), // 정산 예정
                "completedAmount", completed != null ? completed : 0L // 정산 완료
        ));
    }
}