package com.team.wearly.domain.payment.repository;

import com.team.wearly.domain.payment.entity.Settlement;
import com.team.wearly.domain.payment.entity.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findAllByOrderId(String orderId);

    List<Settlement> findAllByStatus(SettlementStatus status);

    @Query("SELECT SUM(s.settlementAmount) FROM Settlement s WHERE s.sellerId = :sellerId AND s.status = :status")
    Long sumAmountBySellerAndStatus(@Param("sellerId") Long sellerId, @Param("status") SettlementStatus status);

    Optional<Settlement> findByOrderIdAndSellerIdAndProductId(String orderId, Long sellerId, Long productId);
}
