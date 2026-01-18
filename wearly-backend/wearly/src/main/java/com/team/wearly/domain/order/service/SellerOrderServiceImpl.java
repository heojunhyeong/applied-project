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
import org.springframework.data.domain.*;
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
     * ✅ pageable 정렬을 강제로 order.createdDate desc로 고정
     * - 프론트에서 sort=createdDate/orderedAt 뭐 보내든 상관없이 정상 동작
     */
    private Pageable forceSortByOrderCreatedDateDesc(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("order.createdDate"))
        );
    }

    @Override
    public Page<SellerOrderDetailListResponse> getSellerOrderDetails(Long sellerId, OrderStatus status, Pageable pageable) {

        Pageable fixedPageable = forceSortByOrderCreatedDateDesc(pageable);

        Page<OrderDetail> page = (status == null)
                ? sellerOrderDetailRepository.findSellerOrderDetails(sellerId, fixedPageable)
                : sellerOrderDetailRepository.findSellerOrderDetailsByStatus(sellerId, status, fixedPageable);

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

                    // ✅ orderedAt = 주문 생성일
                    od.getOrder() != null ? od.getOrder().getCreatedDate() : null
            );
        });
    }

    @Override
    public SellerOrderDetailResponse getSellerOrderDetail(Long sellerId, Long orderDetailId) {

        OrderDetail od = sellerOrderDetailRepository.findDetailByIdAndSellerId(orderDetailId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("주문 상세가 없거나 접근 권한이 없습니다. orderDetailId=" + orderDetailId));

        User buyer = findBuyer(od.getOrder() != null ? od.getOrder().getUserId() : null);

        OrderDeliveryDetail dd = orderDeliveryDetailRepository.findByOrderDetailId(od.getId()).orElse(null);

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

                // orderedAt = 주문 생성일
                od.getOrder() != null ? od.getOrder().getCreatedDate() : null
        );
    }

    @Override
    @Transactional
    public void updateDelivery(Long sellerId, Long orderDetailId, SellerOrderDeliveryUpdateRequest request) {

        OrderDetail od = sellerOrderDetailRepository.findDetailByIdAndSellerId(orderDetailId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException("주문 상세가 없거나 접근 권한이 없습니다. orderDetailId=" + orderDetailId));

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

        // // BEFORE_PAID는 판매자 수정 불가
        if (current == OrderStatus.BEFORE_PAID) {
            throw new IllegalStateException("결제 전 주문은 판매자가 처리할 수 없습니다. current=" + current);
        }

        // // 상태 전이 검증
        if (!isAllowedTransition(current, next)) {
            throw new IllegalStateException("허용되지 않은 상태 변경입니다. " + current + " -> " + next);
        }

        // // CHECK -> IN_DELIVERY로 바뀔 때: 요청에 택배사/송장번호가 오면 저장하고 진행
        if (current == OrderStatus.CHECK && next == OrderStatus.IN_DELIVERY) {

            if (request.carrier() == null) {
                throw new IllegalStateException("택배사는 필수입니다.");
            }
            if (request.invoiceNumber() == null || request.invoiceNumber().isBlank()) {
                throw new IllegalStateException("송장번호는 필수입니다.");
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

        od.updateDetailStatus(next);
    }


    private boolean isAllowedTransition(OrderStatus current, OrderStatus next) {
        // // 결제 완료 후 판매자 처리 시작
        if (current == OrderStatus.PAID && next == OrderStatus.WAIT_CHECK) return true;

        // // 판매자 처리 플로우
        if (current == OrderStatus.WAIT_CHECK && next == OrderStatus.CHECK) return true;
        if (current == OrderStatus.CHECK && next == OrderStatus.IN_DELIVERY) return true;
        if (current == OrderStatus.IN_DELIVERY && next == OrderStatus.DELIVERY_COMPLETED) return true;

        // // (선택) 취소 허용하고 싶으면 열어
        // if (current == OrderStatus.PAID && next == OrderStatus.CANCELLED) return true;
        // if (current == OrderStatus.WAIT_CHECK && next == OrderStatus.CANCELLED) return true;

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
