package com.team.wearly.domain.order.service;

import com.team.wearly.domain.order.dto.request.SellerOrderDeliveryUpdateRequest;
import com.team.wearly.domain.order.dto.request.SellerOrderStatusUpdateRequest;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailResponse;
import com.team.wearly.domain.order.dto.response.SellerOrderListItemResponse;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDelivery;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.SellerOrderDeliveryRepository;
import com.team.wearly.domain.order.repository.SellerOrderRepository;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerOrderService {

    private final SellerOrderRepository sellerOrderRepository;
    private final SellerOrderDeliveryRepository sellerOrderDeliveryRepository;
    private final UserRepository userRepository;

    /*
     * 판매자 주문 목록 조회
     * - status == null : 전체 조회
     * - status != null : 상태별 조회
     */
    public Page<SellerOrderListItemResponse> getSellerOrders(Long sellerId, OrderStatus status, Pageable pageable) {

        Page<Order> page = (status == null)
                ? sellerOrderRepository.findBySellerId(sellerId, pageable)
                : sellerOrderRepository.findBySellerIdAndOrderStatus(sellerId, status, pageable);

        return page.map(order -> {
            long itemCount = (order.getOrderDetails() == null) ? 0 : order.getOrderDetails().size();
            String buyerNickname = getBuyerNickname(order.getUserId());

            return new SellerOrderListItemResponse(
                    order.getOrderId(),
                    buyerNickname,
                    order.getOrderStatus(),
                    order.getTotalPrice(),
                    itemCount,
                    order.getCreatedDate()
            );
        });
    }


    // 판매자 주문 상세 조회
    // orderDetails/product/delivery 같이 조회
    public SellerOrderDetailResponse getSellerOrderDetail(Long sellerId, String orderId) {

        Order order = sellerOrderRepository.findDetailByOrderIdAndSellerId(orderId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문이 없습니다. orderId=" + orderId));

        User buyer = getBuyer(order.getUserId());

        SellerOrderDetailResponse.Delivery deliveryDto = null;
        OrderDelivery delivery = order.getOrderDelivery();
        if (delivery != null) {
            deliveryDto = new SellerOrderDetailResponse.Delivery(
                    delivery.getAddress(),
                    delivery.getDetail_address(),
                    delivery.getZipCode(),
                    delivery.getCarrier(),
                    delivery.getInvoiceNumber()
            );
        }

        List<SellerOrderDetailResponse.Item> items = order.getOrderDetails().stream()
                .map(od -> new SellerOrderDetailResponse.Item(
                        od.getProduct() != null ? od.getProduct().getId() : null,
                        od.getProduct() != null ? od.getProduct().getProductName() : null,
                        od.getProduct() != null ? od.getProduct().getImageUrl() : null,
                        od.getQuantity(),
                        od.getPrice()
                ))
                .toList();

        return new SellerOrderDetailResponse(
                order.getOrderId(),
                order.getOrderStatus(),
                buyer.getUserName(),       // 구매자 로그인 아이디(User.userName)
                buyer.getUserNickname(),   // 구매자 닉네임(User.userNickname)
                order.getTotalPrice(),
                order.getCreatedDate(),
                deliveryDto,
                items
        );
    }


    // 판매자 주문 상태 변경
    @Transactional
    public void updateSellerOrderStatus(Long sellerId, String orderId, SellerOrderStatusUpdateRequest request) {

        Order order = sellerOrderRepository.findDetailByOrderIdAndSellerId(orderId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문이 없습니다. orderId=" + orderId));

        OrderStatus current = order.getOrderStatus();
        OrderStatus next = request.nextStatus();

        boolean allowed =
                (current == OrderStatus.PAID && next == OrderStatus.CHECK) ||
                        (current == OrderStatus.CHECK && next == OrderStatus.IN_DELIVERY) ||
                        (current == OrderStatus.IN_DELIVERY && next == OrderStatus.DELIVERY_COMPLETED);

        if (!allowed) {
            throw new IllegalStateException(
                    "판매자가 변경할 수 없는 주문 상태입니다. (PAID->CHECK, CHECK->IN_DELIVERY, IN_DELIVERY->DELIVERY_COMPLETED만 가능)"
            );
        }

        // CHECK -> IN_DELIVERY 전환 시, '저장된' 택배사/송장번호 필수
        if (current == OrderStatus.CHECK && next == OrderStatus.IN_DELIVERY) {
            validateStoredInvoice(order);
        }

        order.updateStatus(next);
    }


    // 판매자 송장/택배사 입력(수정)

    @Transactional
    public void updateSellerOrderDelivery(Long sellerId, String orderId, SellerOrderDeliveryUpdateRequest request) {

        Order order = sellerOrderRepository.findDetailByOrderIdAndSellerId(orderId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문이 없습니다. orderId=" + orderId));

        OrderDelivery delivery = sellerOrderDeliveryRepository.findByOrder(order)
                .orElseThrow(() -> new IllegalStateException(
                        "배송 정보가 없습니다. (주소/우편번호 포함) 주문 생성 단계에서 생성돼야 합니다."
                ));

        delivery.updateInvoice(request.carrier(), request.invoiceNumber());
    }

    // CHECK -> IN_DELIVERY 전환 직전에 DB에 저장된 값 기준으로 검증
    private void validateStoredInvoice(Order order) {
        OrderDelivery delivery = order.getOrderDelivery();

        if (delivery == null) {
            throw new IllegalStateException("배송 정보가 없습니다. 택배사/송장번호를 먼저 입력하세요.");
        }
        if (delivery.getCarrier() == null) {
            throw new IllegalStateException("택배사는 필수입니다.");
        }
        if (delivery.getInvoiceNumber() == null || delivery.getInvoiceNumber().isBlank()) {
            throw new IllegalStateException("송장번호는 필수입니다.");
        }
    }

    private User getBuyer(Long userId) {
        if (userId == null) {
            throw new IllegalStateException("주문에 userId가 없습니다.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("구매자 정보를 찾을 수 없습니다. userId=" + userId));
    }

    private String getBuyerNickname(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .map(User::getUserNickname)
                .orElse(null);
    }
}
