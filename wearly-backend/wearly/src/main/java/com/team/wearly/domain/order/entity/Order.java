package com.team.wearly.domain.order.entity;

import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.global.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
// order는 SQL 예약어인 경우가 많아 테이블명 명시했음. 참고바람
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외부 노출용 고유 주문 번호 (예: ORD-20240110-UUID)
    @Column(nullable = false, unique = true)
    private String orderId;

    private Long userId;
    //private Long sellerId;

    private Long totalPrice;

    private Long couponDiscountPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 20) // 테스트용
    private OrderStatus orderStatus;

    // 주문 상품들 (1:N)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // 배송 정보 (1:1)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderDelivery orderDelivery;

    // Order.java 내부 연관관계 메서드
    public void addOrderDetail(OrderDetail detail) {
        this.orderDetails.add(detail);
        detail.assignOrder(this);
    }

    public void setOrderDelivery(OrderDelivery delivery) {
        this.orderDelivery = delivery;
        delivery.assignOrder(this);
    }

    public void cancel() {
        if (this.orderStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        this.orderStatus = OrderStatus.CANCELLED;
    }

    // 결제에 따른 상태 변경 메서드
    public void updateStatus(OrderStatus nextStatus) {

        // 같은 상태로 변경 방지(선택)
        if (this.orderStatus == nextStatus) {
            return; // 또는 throw new IllegalArgumentException("이미 해당 상태입니다.");
        }

        // BEFORE_PAID -> PAID or CANCELLED
        if (this.orderStatus == OrderStatus.BEFORE_PAID) {
            if (nextStatus != OrderStatus.PAID && nextStatus != OrderStatus.CANCELLED) {
                throw new IllegalStateException("BEFORE_PAID 상태에서는 PAID 또는 CANCELLED로만 변경할 수 있습니다.");
            }
        }

        this.orderStatus = nextStatus;

        // 결제까지는 OrderDetail 상태도 같이 동기화
        syncAllDetailStatus(nextStatus);
    }

    private void syncAllDetailStatus(OrderStatus status) {
        if (this.orderDetails == null) return;
        for (OrderDetail detail : this.orderDetails) {
            detail.updateDetailStatus(status);
        }
    }
}
