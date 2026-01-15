package com.team.wearly.domain.coupon.controller;

import com.team.wearly.domain.coupon.entity.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    /**
     * 멤버십 전용 쿠폰 다운로드
     * @param benefitId 1: 10% 할인, 2: 5000원 할인
     */
    @PostMapping("/download/{benefitId}")
    public ResponseEntity<String> downloadCoupon(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long benefitId) {

        couponService.downloadMembershipCoupon(userId, benefitId);
        return ResponseEntity.ok("쿠폰 발급 완료!");
    }
}