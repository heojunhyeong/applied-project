package com.team.wearly.domain.membership.repository;

import com.team.wearly.domain.membership.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership,Long> {
    Optional<Membership> findByOrderId(String orderId);
}
