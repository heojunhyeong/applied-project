package com.team.wearly.domain.coupon.controller;

import com.team.wearly.domain.coupon.service.CouponService;
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
     * 멤버십 회원을 대상으로 특정 혜택 쿠폰을 발급하는 API
     *
     * @param userId 쿠폰을 다운로드하는 사용자의 식별자
     * @param benefitId 발급받으려는 혜택(쿠폰)의 ID
     * @return 쿠폰 발급 완료 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @PostMapping("/download/{benefitId}")
    public ResponseEntity<String> downloadCoupon(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long benefitId) {

        couponService.downloadMembershipCoupon(userId, benefitId);
        return ResponseEntity.ok("쿠폰 발급 완료!");
    }
}