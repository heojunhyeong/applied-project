package com.team.wearly.domain.membership.entity;

import com.team.wearly.domain.membership.entity.enums.CouponStatus;
import com.team.wearly.domain.membership.entity.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // benefit_id: FK (필요시 다른 엔티티와 매핑)
    private Long benefitId;

    // order_id: FK, 어떤 주문에 쿠폰이 적용되었는지 확인 가능
    private Long orderId;

    @Column(length = 255, nullable = false)
    private String couponCode;  // coupon_code

    @Column(length = 255, nullable = false)
    private CouponType couponType;  // coupon_type (enum 참조 예정)

    @Column(nullable = false)
    private LocalDateTime expirationDate;  // 쿠폰 만료일

    @Column(length = 255, nullable = false)
    private CouponStatus couponStatus;  // coupon_status (enum 참조 예정)

    @Column(nullable = false)
    private Long minOrderPrice;  // 최소 사용 가능 금액

    @Column
    private LocalDateTime usedAt;  // 실제 사용 일시, NULL 가능

}