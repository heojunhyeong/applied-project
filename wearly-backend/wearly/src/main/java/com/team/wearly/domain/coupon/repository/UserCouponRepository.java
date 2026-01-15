package com.team.wearly.domain.coupon.repository;

import com.team.wearly.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Optional<UserCoupon> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndBenefitId(Long userId, Long benefitId);
}
