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

@RestController
@RequestMapping("/api/seller/settlements")
@RequiredArgsConstructor
public class SellerSettlementController {

    private final SettlementRepository settlementRepository;

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