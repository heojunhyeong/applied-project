package com.team.wearly.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    //0으로 시작하는 송장번호 고려해 String으로 변경
    private String invoiceNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    // Order 엔티티에서 호출할 내부용 메서드
    protected void assignOrder(Order order) {
        this.order = order;
    }
}