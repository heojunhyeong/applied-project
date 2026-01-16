package com.team.wearly.domain.coupon.service;

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

    /**
     * 멤버십 활성 사용자인지 확인 후 전용 혜택 쿠폰을 생성하고 저장하는 서비스 로직
     *
     * @param userId 쿠폰을 발급받을 사용자의 식별자
     * @param benefitId 발급할 쿠폰의 종류를 결정하는 혜택 ID
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Transactional
    public void downloadMembershipCoupon(Long userId, Long benefitId) {

        Membership membership = membershipRepository.findByUser_Id(userId)
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