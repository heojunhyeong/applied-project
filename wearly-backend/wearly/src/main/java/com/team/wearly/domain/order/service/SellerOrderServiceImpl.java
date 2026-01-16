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
     * 특정 판매자에게 들어온 주문 상세 목록을 페이징하여 조회함 (상태별 필터링 지원)
     *
     * @param sellerId 판매자 식별자
     * @param status   필터링할 주문 상태 (null일 경우 전체 조회)
     * @param pageable 페이징 정보
     * @return 주문 정보 및 배송 현황을 포함한 DTO 페이지
     * @author 허보미
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
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
     * 판매자가 특정 주문 상세 내역을 확인하며, 구매자 정보 및 배송지 주소를 함께 조회함
     *
     * @param sellerId      판매자 식별자
     * @param orderDetailId 개별 주문 상세 식별자
     * @return 배송지와 상품 정보를 포함한 상세 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
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
     * 판매자가 상품 발송을 위해 택배사 정보와 송장 번호를 입력하거나 수정함
     *
     * @param sellerId      판매자 식별자
     * @param orderDetailId 주문 상세 식별자
     * @param request       택배사 및 송장 번호 정보
     * @author 허보미
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
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
     * 판매자가 주문의 진행 상태를 변경함 (상품 확인 -> 배송 중 -> 배송 완료 순차 변경)
     *
     * @param sellerId      판매자 식별자
     * @param orderDetailId 주문 상세 식별자
     * @param request       변경하고자 하는 목표 상태 정보
     * @author 허보미
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
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
