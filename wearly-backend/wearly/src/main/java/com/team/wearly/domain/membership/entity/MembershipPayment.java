package com.team.wearly.domain.membership.entity;

import com.team.wearly.domain.payment.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String billingKey;

    private String impUid;

    @CreatedDate
    private LocalDateTime paidAt;

    private PaymentStatus paymentStatus;

    private String receiptUrl;



//    private Long membershipOrderId;
}
