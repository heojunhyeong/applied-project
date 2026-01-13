package com.team.wearly.domain.payment.repository;

import com.team.wearly.domain.payment.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}