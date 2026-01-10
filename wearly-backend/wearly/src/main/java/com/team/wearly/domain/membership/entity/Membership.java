package com.team.wearly.domain.membership.entity;

import com.team.wearly.domain.membership.entity.enums.MembershipStatus;
import com.team.wearly.domain.membership.entity.enums.SubscribeType;
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
@EntityListeners(JpaAuditingConfig.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private SubscribeType subscribeType;

    private MembershipStatus status;

    // 외부 노출용 고유 주문 번호 (예: ORD-20240110-UUID)
    @Column(nullable = false, unique = true)
    private String orderId;

    @CreatedDate
    private LocalDateTime startDate;

    @LastModifiedDate
    private LocalDateTime endDate;

    private LocalDateTime nextPaymentDate;

    private LocalDateTime unsubscribeDate;

    // 결제에 따른 상태 변경 메서드
    public void updateStatus(MembershipStatus status) {
        this.status = status;
    }








//    private Long userId;
//    private Long lastPaymentId;
}
