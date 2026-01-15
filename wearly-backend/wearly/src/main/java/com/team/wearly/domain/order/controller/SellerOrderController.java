package com.team.wearly.domain.order.controller;

import com.team.wearly.domain.order.dto.request.SellerOrderDeliveryUpdateRequest;
import com.team.wearly.domain.order.dto.request.SellerOrderDetailStatusUpdateRequest;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailListResponse;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailResponse;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.service.SellerOrderService;
import com.team.wearly.domain.user.entity.Seller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
public class SellerOrderController {

    private final SellerOrderService sellerOrderService;

    /**
     * Authentication principal = Seller 엔티티
     */
    private Seller getSeller(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Seller)) {
            throw new IllegalStateException("SELLER 계정만 접근 가능합니다.");
        }
        return (Seller) principal;
    }

    /**
     * 1) 판매자 주문 목록 조회 (OrderDetail 기준)
     * - status 없으면 전체
     * - status 있으면 detailStatus 기준 필터
     */
    @GetMapping
    public ResponseEntity<Page<SellerOrderDetailListResponse>> getSellerOrderDetails(
            Authentication authentication,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(
                sellerOrderService.getSellerOrderDetails(seller.getId(), status, pageable)
        );
    }

    /**
     * 2) 판매자 주문 상세 조회 (OrderDetail 단건)
     */
    @GetMapping("/{orderDetailId}")
    public ResponseEntity<SellerOrderDetailResponse> getSellerOrderDetail(
            Authentication authentication,
            @PathVariable Long orderDetailId
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(
                sellerOrderService.getSellerOrderDetail(seller.getId(), orderDetailId)
        );
    }

    /**
     * 3) 송장/택배사 입력(수정) - OrderDetail 단위
     * - 서비스에서 CHECK 상태에서만 입력 허용하도록 막아둠(원하면 완화 가능)
     */
    @PatchMapping("/{orderDetailId}/delivery")
    public ResponseEntity<Void> updateDelivery(
            Authentication authentication,
            @PathVariable Long orderDetailId,
            @Valid @RequestBody SellerOrderDeliveryUpdateRequest request
    ) {
        Seller seller = getSeller(authentication);
        sellerOrderService.updateDelivery(seller.getId(), orderDetailId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 4) 주문 상태 변경 - OrderDetail 단위
     * - 서비스에서 전이 규칙/송장 필수 검증 처리
     */
    @PatchMapping("/{orderDetailId}/status")
    public ResponseEntity<Void> updateStatus(
            Authentication authentication,
            @PathVariable Long orderDetailId,
            @Valid @RequestBody SellerOrderDetailStatusUpdateRequest request
    ) {
        Seller seller = getSeller(authentication);
        sellerOrderService.updateStatus(seller.getId(), orderDetailId, request);
        return ResponseEntity.noContent().build();
    }
}
