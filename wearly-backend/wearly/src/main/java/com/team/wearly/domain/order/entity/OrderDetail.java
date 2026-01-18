package com.team.wearly.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.Size;
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
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;
    private Long price;

    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(mappedBy = "orderDetail", cascade = CascadeType.ALL)
    private OrderDeliveryDetail deliveryDetail;

    @Enumerated(EnumType.STRING)
    private Size size;

    public void setDeliveryDetail(OrderDeliveryDetail deliveryDetail) {
        this.deliveryDetail = deliveryDetail;
        deliveryDetail.assignOrderDetail(this);
    }

    // Order 엔티티에서 호출할 내부용 메서드
    protected void assignOrder(Order order) {
        this.order = order;
    }

    private OrderStatus detailStatus;

    // 디테일 상태 변경 메서드
    public void updateDetailStatus(OrderStatus nextStatus) {
        this.detailStatus = nextStatus;
    }
}
