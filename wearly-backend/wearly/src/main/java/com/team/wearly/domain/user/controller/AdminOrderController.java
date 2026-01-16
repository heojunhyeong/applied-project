package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.response.AdminOrderListResponse;
import com.team.wearly.domain.user.dto.response.AdminOrderResponse;
import com.team.wearly.domain.user.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    /**
     * 관리자용 주문 내역 조회 API (간단한 정보만)
     * [사용 예시]
     * 1. 전체 조회: GET /api/admin/orders
     * 2. 닉네임 검색: GET /api/admin/orders?nickname=검색어
     * 
     * 반환 정보:
     * - orderId: 주문 ID
     * - orderNumber: 주문 번호
     * - userId: 회원 ID
     * - paymentStatus: 결제 내역 (O 또는 X)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AdminOrderListResponse>> getOrders(
            @RequestParam(required = false) String nickname) {
        
        List<AdminOrderListResponse> response = adminOrderService.getOrders(nickname);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자용 주문 상세 조회 API
     * GET /api/admin/orders/{orderId}
     * 
     * 반환 정보:
     * - orderId, orderNumber, userId, userNickname, userEmail
     * - orderDate, totalPrice, couponDiscountPrice, finalPrice
     * - orderStatus, deliveryStatus, isPaid
     * - paymentInfo (exists, status, amount, paymentMethod, paymentDate)
     * - orderItems (productId, productName, imageUrl, quantity, price, totalItemPrice)
     * - deliveryInfo (주석 처리됨)
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminOrderResponse> getOrder(@PathVariable Long orderId) {
        AdminOrderResponse response = adminOrderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }
}
