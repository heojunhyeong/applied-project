package com.team.wearly.domain.membership.entity;

import com.team.wearly.domain.membership.entity.enums.Benefit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "membership_benefit")
@Getter
@NoArgsConstructor // JPA용 기본 생성자, 외부 호출 막음
@AllArgsConstructor   // 빌더에서만 사용
@Builder
public class MembershipBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 255, nullable = false)
    private Benefit benefitType;

    @Column(nullable = false)
    private Long discountRate;

    //    private Long membershipOrderId;

}