package com.team.wearly.domain.payment.entity;

import com.team.wearly.domain.payment.entity.enums.SettlementStatus;
import com.team.wearly.global.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

    @Entity
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class Settlement extends BaseTimeEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String orderId;
        private Long sellerId;
        private Long totalAmount;
        private Long commission;
        private Long settlementAmount;

        @Enumerated(EnumType.STRING)
        private SettlementStatus status;
    }

