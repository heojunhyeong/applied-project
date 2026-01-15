package com.team.wearly.domain.order.service;

import com.team.wearly.domain.order.dto.request.SellerOrderDeliveryUpdateRequest;
import com.team.wearly.domain.order.dto.request.SellerOrderDetailStatusUpdateRequest;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailListResponse;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailResponse;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerOrderService {

    // 판매자 주문 목록 (OrderDetail 기준)
    Page<SellerOrderDetailListResponse> getSellerOrderDetails(
            Long sellerId,
            OrderStatus status,
            Pageable pageable
    );

    // 판매자 주문 단건 상세
    SellerOrderDetailResponse getSellerOrderDetail(
            Long sellerId,
            Long orderDetailId
    );

    // 송장 / 택배사 입력
    void updateDelivery(
            Long sellerId,
            Long orderDetailId,
            SellerOrderDeliveryUpdateRequest request
    );

    // 주문 상태 변경 (WAIT_CHECK 이후부터 판매자 권한)
    void updateStatus(
            Long sellerId,
            Long orderDetailId,
            SellerOrderDetailStatusUpdateRequest request
    );
}
