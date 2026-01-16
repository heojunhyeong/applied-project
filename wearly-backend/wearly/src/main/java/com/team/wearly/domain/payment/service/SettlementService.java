package com.team.wearly.domain.payment.service;

import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.payment.entity.Settlement;
import com.team.wearly.domain.payment.entity.enums.SettlementStatus;
import com.team.wearly.domain.payment.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.util.List;


/**
 * 주문 상세 정보를 바탕으로 판매자의 정산 금액을 계산하고 상태를 관리하는 서비스
 *
 * @author 허준형
 * @DateOfCreated 2026-01-15
 * @DateOfEdit 2026-01-15
 */
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final double COMMISSION_RATE = 0.1; // 수수료 10%

    /**
     * 주문 내 개별 상품의 원가를 기준으로 플랫폼 수수료를 제외한 판매자별 정산 데이터를 생성함
     *
     * @param order 결제가 완료된 주문 엔티티
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
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

    /**
     * 특정 주문에 포함된 모든 정산 데이터의 상태를 지급 확정(CONFIRMED)으로 변경함
     *
     * @param orderId 정산을 확정할 주문 번호
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Transactional
    public void markAsSettlementTarget(String orderId) {
        List<Settlement> settlements = settlementRepository.findAllByOrderId(orderId);
        settlements.forEach(s -> s.updateStatus(SettlementStatus.CONFIRMED));
    }

    /**
     * 주문 취소 시 해당 주문과 연관된 모든 정산 데이터를 취소 상태로 변경함
     *
     * @param orderId 취소된 주문 번호
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Transactional
    public void cancelSettlement(String orderId) {
        List<Settlement> settlements = settlementRepository.findAllByOrderId(orderId);
        settlements.forEach(s -> s.updateStatus(SettlementStatus.CANCELLED));
    }

    /**
     * 사용자의 구매 확정 시 주문 내 특정 상품 품목에 대해서만 정산 상태를 확정으로 변경함
     *
     * @param orderId 주문 번호
     * @param sellerId 판매자 식별자
     * @param productId 상품 식별자
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Transactional
    public void markItemAsSettlementTarget(String orderId, Long sellerId, Long productId) {
        Settlement settlement = settlementRepository.findByOrderIdAndSellerIdAndProductId(orderId, sellerId, productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품에 대한 정산 데이터를 찾을 수 없습니다."));

        settlement.updateStatus(SettlementStatus.CONFIRMED);

    }
}