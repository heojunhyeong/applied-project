package com.team.wearly.domain.order.entity;

import com.team.wearly.domain.order.entity.enums.Carrier;
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
public class OrderDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private String detail_address;

    private Long zipCode;

    @Enumerated(EnumType.STRING)
    private Carrier carrier;

    private Long invoiceNumber;

    //    private Long orderId;
}
