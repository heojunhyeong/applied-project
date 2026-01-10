package com.team.wearly.domain.payment.entity;

import com.team.wearly.domain.membership.entity.enums.PaymentStatus;
import com.team.wearly.domain.payment.entity.enums.PaymentMethod;
import com.team.wearly.global.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseTimeEntity { // 생성/수정일 상속 권장

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 토스에서 발급하는 결제 고유 키
    @Column(nullable = false, unique = true)
    private String paymentKey;

    // 우리 시스템에서 생성한 주문 번호 (UUID 등)
    @Column(nullable = false, unique = true)
    private String orderId;

    // 실 결제 금액
    private Long amount;

    // READY, DONE, CANCELED 등
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
}