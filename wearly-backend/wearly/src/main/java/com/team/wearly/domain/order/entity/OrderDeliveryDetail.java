package com.team.wearly.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.wearly.domain.order.entity.enums.Carrier;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Carrier carrier;

    // 0으로 시작 고려 String
    private String invoiceNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false, unique = true)
    @JsonIgnore
    private OrderDetail orderDetail;

    public void assignOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public void updateInvoice(Carrier carrier, String invoiceNumber) {
        this.carrier = carrier;
        this.invoiceNumber = invoiceNumber;
    }
}
