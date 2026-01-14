package com.team.wearly.domain.user.controller;

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
     * 관리자용 주문 내역 조회 API
     * [사용 예시]
     * 1. 전체 조회: GET /api/admin/orders
     * 2. 닉네임 검색: GET /api/admin/orders?nickname=검색어
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AdminOrderResponse>> getOrders(
            @RequestParam(required = false) String nickname) {
        
        List<AdminOrderResponse> response = adminOrderService.getOrders(nickname);
        return ResponseEntity.ok(response);
    }
}
