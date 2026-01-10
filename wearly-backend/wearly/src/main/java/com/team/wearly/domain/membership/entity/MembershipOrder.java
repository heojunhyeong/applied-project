package com.team.wearly.domain.membership.entity;

import com.team.wearly.domain.membership.entity.enums.MembershipOrderStatus;
import com.team.wearly.domain.membership.entity.enums.SubscribeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private SubscribeType subscribeType;

    private MembershipOrderStatus status;

    private Long totalAmount;


//    private Long userId;
}
