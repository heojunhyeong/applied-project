package com.team.wearly.domain.payment.service;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.payment.entity.Settlement;
import com.team.wearly.domain.payment.entity.enums.SettlementStatus;
import com.team.wearly.domain.payment.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final double COMMISSION_RATE = 0.1; // 수수료 10%

    /**
     * 플랫폼 손해 감수형 정산 데이터 생성 로직
     * 사용자가 할인을 받았어도 판매자에게는 OrderDetail에 기록된 원가(price)를 기준으로 정산함
     * TODO: 주석 추가 예정
     */
    @Transactional
    public void createSettlementData(Order order) {
        for (OrderDetail detail : order.getOrderDetails()) {
            // 판매자 정산 기준: 상품 원가 * 수량
            Long itemOriginalTotal = detail.getPrice() * detail.getQuantity();

            // 수수료 계산 (원가 기준 10%)
            Long commission = (long) (itemOriginalTotal * COMMISSION_RATE);

            // 판매자 지급액 = 원가 - 수수료
            Long settlementAmount = itemOriginalTotal - commission;

            // 정산 데이터 저장
            Settlement settlement = Settlement.builder()
                    .orderId(order.getOrderId())
                    .sellerId(detail.getSellerId())
                    .totalAmount(itemOriginalTotal)
                    .commission(commission)
                    .settlementAmount(settlementAmount)
                    .status(SettlementStatus.READY)
                    .build();

            settlementRepository.save(settlement);
        }


    }

    @Transactional
    public void markAsSettlementTarget(String orderId) {
        List<Settlement> settlements = settlementRepository.findAllByOrderId(orderId);
        settlements.forEach(s -> s.updateStatus(SettlementStatus.CONFIRMED));
    }


    @Transactional
    public void cancelSettlement(String orderId) {
        List<Settlement> settlements = settlementRepository.findAllByOrderId(orderId);
        settlements.forEach(s -> s.updateStatus(SettlementStatus.CANCELLED));
    }
}