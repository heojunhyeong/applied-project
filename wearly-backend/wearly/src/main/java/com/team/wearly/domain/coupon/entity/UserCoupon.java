package com.team.wearly.domain.coupon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.wearly.domain.coupon.entity.enums.CouponStatus;
import com.team.wearly.domain.coupon.entity.enums.CouponType;
import com.team.wearly.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long benefitId;

    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String couponCode;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "couponstatus")
    private CouponStatus status;

    private Long minOrderPrice;

    private Long discountValue;

    private LocalDateTime usedAt;

    // 쿠폰 사용 가능 여부 체크
    public boolean isAvailable(Long totalPrice) {
        return this.status == CouponStatus.UNUSED &&
                this.expirationDate.isAfter(LocalDateTime.now()) &&
                totalPrice >= this.minOrderPrice;
    }

    // 쿠폰 적용 처리
    public void applyToOrder(String orderId) {
        this.status = CouponStatus.USED;
        this.orderId = orderId;
        this.usedAt = LocalDateTime.now();
    }
}