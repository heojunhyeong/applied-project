package com.team.wearly.domain.order.controller;

import com.team.wearly.domain.order.dto.request.SellerOrderDeliveryUpdateRequest;
import com.team.wearly.domain.order.dto.request.SellerOrderStatusUpdateRequest;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailResponse;
import com.team.wearly.domain.order.dto.response.SellerOrderListItemResponse;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.service.SellerOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/orders")
public class SellerOrderController {

    private final SellerOrderService sellerOrderService;

    // 판매자 주문 목록 조회
    @GetMapping
    public ResponseEntity<Page<SellerOrderListItemResponse>> getSellerOrders(
            @RequestHeader("Seller-Id") Long sellerId,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(sellerOrderService.getSellerOrders(sellerId, status, pageable));
    }

    // 판매자 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<SellerOrderDetailResponse> getSellerOrderDetail(
            @RequestHeader("Seller-Id") Long sellerId,
            @PathVariable String orderId
    ) {
        return ResponseEntity.ok(sellerOrderService.getSellerOrderDetail(sellerId, orderId));
    }

    // 판매자 송장/택배사 입력(수정)
    @PatchMapping("/{orderId}/delivery")
    public ResponseEntity<Void> updateSellerOrderDelivery(
            @RequestHeader("Seller-Id") Long sellerId,
            @PathVariable String orderId,
            @Valid @RequestBody SellerOrderDeliveryUpdateRequest request
    ) {
        sellerOrderService.updateSellerOrderDelivery(sellerId, orderId, request);
        return ResponseEntity.noContent().build();
    }

    // 판매자 주문 상태 변경
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateSellerOrderStatus(
            @RequestHeader("Seller-Id") Long sellerId,
            @PathVariable String orderId,
            @Valid @RequestBody SellerOrderStatusUpdateRequest request
    ) {
        sellerOrderService.updateSellerOrderStatus(sellerId, orderId, request);
        return ResponseEntity.noContent().build();
    }
}
