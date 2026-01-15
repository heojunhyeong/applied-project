package com.team.wearly.domain.coupon.entity.service;

import com.team.wearly.domain.coupon.entity.UserCoupon;
import com.team.wearly.domain.coupon.entity.enums.CouponStatus;
import com.team.wearly.domain.coupon.entity.enums.CouponType;
import com.team.wearly.domain.coupon.repository.UserCouponRepository;
import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final UserCouponRepository userCouponRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository; // User 엔티티를 찾기 위해 필요

    @Transactional
    public void downloadMembershipCoupon(Long userId, Long benefitId) {

        Membership membership = membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("멤버십 회원이 아닙니다."));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("멤버십 결제 후 이용 가능합니다.");
        }

        if (userCouponRepository.existsByUserIdAndBenefitId(userId, benefitId)) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserCoupon welcomeCoupon = UserCoupon.builder()
                .user(user) // UserCoupon 엔티티에 정의된 @ManyToOne User
                .benefitId(benefitId)
                .status(CouponStatus.UNUSED)
                .expirationDate(LocalDateTime.now().plusDays(30))
                .couponCode("MEM-" + benefitId + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase())
                .couponType(benefitId == 1L ? CouponType.DISCOUNT_RATE : CouponType.DISCOUNT_AMOUNT)
                // 1번은 10%, 2번은 5천원
                .discountValue(benefitId == 1L ? 10L : 5000L)
                // 최소 주문금액, 1번은 만원, 2번은 3만원
                .minOrderPrice(benefitId == 1L ? 10000L : 30000L)
                .build();

        userCouponRepository.save(welcomeCoupon);
    }
}