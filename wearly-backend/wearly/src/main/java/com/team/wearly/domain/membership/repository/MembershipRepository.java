package com.team.wearly.domain.membership.repository;

import com.team.wearly.domain.membership.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership,Long> {
}
