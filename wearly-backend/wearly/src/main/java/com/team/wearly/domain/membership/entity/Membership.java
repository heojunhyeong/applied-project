package com.team.wearly.domain.membership.entity;

import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.entity.enums.SubscribeType;
import com.team.wearly.global.common.domain.BaseTimeEntity;
import com.team.wearly.global.config.JpaAuditingConfig;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String orderId; // 결제 검증용

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    private String billingKey;

    private LocalDateTime nextPaymentDate;

    public void updateStatus(MembershipStatus status) {
        this.status = status;
    }

    public void registerBillingInfo(String billingKey) {
        this.billingKey = billingKey;
        this.status = MembershipStatus.ACTIVE;
        this.nextPaymentDate = LocalDateTime.now().plusMonths(1); // 1달 뒤 결제 예정
    }
}
