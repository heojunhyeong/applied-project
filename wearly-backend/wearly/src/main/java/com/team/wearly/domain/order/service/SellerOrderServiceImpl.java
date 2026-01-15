package com.team.wearly.domain.order.service;

import com.team.wearly.domain.order.dto.request.SellerOrderDeliveryUpdateRequest;
import com.team.wearly.domain.order.dto.request.SellerOrderDetailStatusUpdateRequest;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailListResponse;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailResponse;
import com.team.wearly.domain.order.entity.OrderDeliveryDetail;
import com.team.wearly.domain.order.entity.OrderDetail;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.OrderDeliveryDetailRepository;
import com.team.wearly.domain.order.repository.SellerOrderDetailRepository;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerOrderServiceImpl implements SellerOrderService {

    private final SellerOrderDetailRepository sellerOrderDetailRepository;
    private final OrderDeliveryDetailRepository orderDeliveryDetailRepository;
    private final UserRepository userRepository;

    /**
     * 판매자 주문 목록 (OrderDetail 기준)
     * - status=null 이면 전체
     * - status!=null 이면 detailStatus 필터
     */
    @Override
    public Page<SellerOrderDetailListResponse> getSellerOrderDetails(Long sellerId, OrderStatus status, Pageable pageable) {

        Page<OrderDetail> page = (status == null)
                ? sellerOrderDetailRepository.findSellerOrderDetails(sellerId, pageable)
                : sellerOrderDetailRepository.findSellerOrderDetailsByStatus(sellerId, status, pageable);

        return page.map(od -> {
            User buyer = findBuyer(od.getOrder() != null ? od.getOrder().getUserId() : null);

            OrderDeliveryDetail dd = orderDeliveryDetailRepository.findByOrderDetailId(od.getId()).orElse(null);

            return new SellerOrderDetailListResponse(
                    od.getId(),
                    od.getOrder() != null ? od.getOrder().getOrderId() : null,

                    buyer != null ? buyer.getUserName() : null,
                    buyer != null ? buyer.getUserNickname() : null,

                    od.getOrder() != null ? od.getOrder().getOrderStatus() : null,
                    od.getDetailStatus(),

                    od.getProduct() != null ? od.getProduct().getId() : null,
                    od.getProduct() != null ? od.getProduct().getProductName() : null,
                    od.getProduct() != null ? od.getProduct().getImageUrl() : null,

                    od.getQuantity(),
                    od.getPrice(),

                    dd != null ? dd.getCarrier() : null,
                    dd != null ? dd.getInvoiceNumber() : null,

                    od.getOrder() != null ? od.getOrder().getCreatedDate() : null
            );
        });
    }

    /**
     * 판매자 주문 상세 (OrderDetail 단건)
     */
    @Override
    public SellerOrderDetailResponse getSellerOrderDetail(Long sellerId, Long orderDetailId) {

        OrderDetail od = sellerOrderDetailRepository.findDetailByIdAndSellerId(orderDetailId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("주문 상세가 없거나 접근 권한이 없습니다. orderDetailId=" + orderDetailId));

        User buyer = findBuyer(od.getOrder() != null ? od.getOrder().getUserId() : null);

        OrderDeliveryDetail dd = orderDeliveryDetailRepository.findByOrderDetailId(od.getId()).orElse(null);

        // 배송지(구매자 입력, 읽기 전용) - Order.orderDelivery 에서 꺼냄
        SellerOrderDetailResponse.DeliveryAddress addressDto = null;
        if (od.getOrder() != null && od.getOrder().getOrderDelivery() != null) {
            var d = od.getOrder().getOrderDelivery();
            addressDto = new SellerOrderDetailResponse.DeliveryAddress(
                    d.getAddress(),
                    d.getDetail_address(),
                    d.getZipCode()
            );
        }

        SellerOrderDetailResponse.ProductInfo productDto = null;
        if (od.getProduct() != null) {
            productDto = new SellerOrderDetailResponse.ProductInfo(
                    od.getProduct().getId(),
                    od.getProduct().getProductName(),
                    od.getProduct().getImageUrl()
            );
        }

        return new SellerOrderDetailResponse(
                od.getId(),
                od.getOrder() != null ? od.getOrder().getOrderId() : null,

                buyer != null ? buyer.getUserName() : null,
                buyer != null ? buyer.getUserNickname() : null,

                od.getOrder() != null ? od.getOrder().getOrderStatus() : null,
                od.getDetailStatus(),

                productDto,
                addressDto,

                dd != null ? dd.getCarrier() : null,
                dd != null ? dd.getInvoiceNumber() : null,

                od.getQuantity(),
                od.getPrice(),

                od.getOrder() != null ? od.getOrder().getCreatedDate() : null
        );
    }

    /**
     * 송장/택배사 입력(수정) - OrderDetail 단위
     * - 상태 제한은 최소로만: 보통 CHECK에서만 입력 허용(원하면 완화 가능)
     */
    @Override
    @Transactional
    public void updateDelivery(Long sellerId, Long orderDetailId, SellerOrderDeliveryUpdateRequest request) {

        OrderDetail od = sellerOrderDetailRepository.findDetailByIdAndSellerId(orderDetailId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("주문 상세가 없거나 접근 권한이 없습니다. orderDetailId=" + orderDetailId));

        // 필요하면 여기서 상태 제한 강제
        // CHECK 상태에서만 송장 입력 가능 (원치 않으면 if 블록 삭제)
        if (od.getDetailStatus() != OrderStatus.CHECK) {
            throw new IllegalStateException("송장 입력은 CHECK 상태에서만 가능합니다.");
        }

        OrderDeliveryDetail deliveryDetail = orderDeliveryDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseGet(() -> {
                    OrderDeliveryDetail created = OrderDeliveryDetail.builder().build();
                    created.assignOrderDetail(od);
                    return created;
                });

        deliveryDetail.updateInvoice(request.carrier(), request.invoiceNumber());
        orderDeliveryDetailRepository.save(deliveryDetail);
    }

    /**
     * 주문 상태 변경 (WAIT_CHECK 이후부터 판매자 권한)
     * - 허용 전이: WAIT_CHECK -> CHECK -> IN_DELIVERY -> DELIVERY_COMPLETED
     * - CHECK -> IN_DELIVERY 는 송장/택배사 필수
     */
    @Override
    @Transactional
    public void updateStatus(Long sellerId, Long orderDetailId, SellerOrderDetailStatusUpdateRequest request) {

        OrderDetail od = sellerOrderDetailRepository.findDetailByIdAndSellerId(orderDetailId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("주문 상세가 없거나 접근 권한이 없습니다. orderDetailId=" + orderDetailId));

        OrderStatus current = od.getDetailStatus();
        OrderStatus next = request.nextStatus();

        if (current == null) {
            throw new IllegalStateException("detailStatus가 비어있습니다. 주문 생성 시 동기화가 필요합니다.");
        }

        // 판매자 권한 시작 지점: WAIT_CHECK부터
        if (current == OrderStatus.BEFORE_PAID || current == OrderStatus.PAID) {
            throw new IllegalStateException("아직 판매자가 처리할 수 없는 주문 상태입니다. current=" + current);
        }

        // 전이 규칙(엄격)
        if (!isAllowedTransition(current, next)) {
            throw new IllegalStateException("허용되지 않은 상태 변경입니다. " + current + " -> " + next);
        }

        // 발송 처리 시 송장 필수
        if (current == OrderStatus.CHECK && next == OrderStatus.IN_DELIVERY) {
            validateInvoiceRequired(orderDetailId);
        }

        od.updateDetailStatus(next);
    }

    private boolean isAllowedTransition(OrderStatus current, OrderStatus next) {
        // WAIT_CHECK -> CHECK
        if (current == OrderStatus.WAIT_CHECK && next == OrderStatus.CHECK) return true;

        // CHECK -> IN_DELIVERY
        if (current == OrderStatus.CHECK && next == OrderStatus.IN_DELIVERY) return true;

        // IN_DELIVERY -> DELIVERY_COMPLETED
        if (current == OrderStatus.IN_DELIVERY && next == OrderStatus.DELIVERY_COMPLETED) return true;

        return false;
    }

    private void validateInvoiceRequired(Long orderDetailId) {
        OrderDeliveryDetail dd = orderDeliveryDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseThrow(() -> new IllegalStateException("송장 정보가 없습니다. 택배사/송장번호를 먼저 입력하세요."));

        if (dd.getCarrier() == null) {
            throw new IllegalStateException("택배사는 필수입니다.");
        }
        if (dd.getInvoiceNumber() == null || dd.getInvoiceNumber().isBlank()) {
            throw new IllegalStateException("송장번호는 필수입니다.");
        }
    }

    private User findBuyer(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId).orElse(null);
    }
}
