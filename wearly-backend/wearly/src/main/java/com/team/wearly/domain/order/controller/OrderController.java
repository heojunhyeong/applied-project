package com.team.wearly.domain.order.controller;

import com.team.wearly.domain.order.dto.response.OrderDetailResponse;
import com.team.wearly.domain.order.dto.response.OrderHistoryResponse;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;
import com.team.wearly.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 프론트엔드에서 주문 번호를 알려주는 API
     *
     * @param request 주문 상품의 정보
     *
     * @return 주문이 성공하였을때 성공
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2025-01-11
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(
            Authentication authentication,
            @RequestBody OrderCreateRequest request) {

        Long userId = Long.parseLong(authentication.getName());
//        Long userId = 1L; 테스트용
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderHistoryResponse>> getMyOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }
}