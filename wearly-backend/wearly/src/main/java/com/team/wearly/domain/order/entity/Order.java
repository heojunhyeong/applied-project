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

    private Long totalPrice;

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

    // 결제에 따른 상태 변경 메서드
    public void updateStatus(OrderStatus status) {
        // 이미 결제 완료된 건을 다시 결제 완료로 바꾸려 할 때 에러 발생
        if (this.orderStatus == OrderStatus.PAID && status == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제가 완료된 주문입니다.");
        }
        this.orderStatus = status;
    }
    public void cancel() {
        if (this.orderStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        this.orderStatus = OrderStatus.CANCELLED;
    }


}
