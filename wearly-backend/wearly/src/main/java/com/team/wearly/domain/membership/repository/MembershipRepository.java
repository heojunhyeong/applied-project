package com.team.wearly.domain.membership.repository;

import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership,Long> {
    Optional<Membership> findByOrderId(String orderId);
    Optional<Membership> findByUserId(Long userId);
    // 상태가 ACTIVE이고, 다음 결제일이 오늘인 목록 조회
    List<Membership> findAllByStatusAndNextPaymentDateBefore(MembershipStatus status, LocalDateTime dateTime);
}
