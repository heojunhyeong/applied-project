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
     * 전체 주문 목록을 조회하거나 특정 닉네임을 가진 사용자의 주문을 검색함
     * [보안] ROLE_ADMIN 권한을 가진 사용자만 접근 가능
     *
     * @param nickname 검색 조건으로 사용할 사용자의 닉네임 (Optional)
     * @return 주문 요약 정보(ID, 주문번호, 회원ID, 결제여부) 리스트
     * @author 최윤혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AdminOrderListResponse>> getOrders(
            @RequestParam(required = false) String nickname) {
        
        List<AdminOrderListResponse> response = adminOrderService.getOrders(nickname);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 주문의 상세 정보(구매자 인적사항, 결제 정보, 주문 상품 목록 등)를 조회함
     * [보안] ROLE_ADMIN 권한을 가진 사용자만 접근 가능
     *
     * @param orderId 상세 조회할 주문의 시스템 식별자
     * @return 주문 상세 내역 및 결제/배송 상태 정보 DTO
     * @author 최윤혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminOrderResponse> getOrder(@PathVariable Long orderId) {
        AdminOrderResponse response = adminOrderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자 권한으로 주문을 취소함
     * [보안] ROLE_ADMIN 권한을 가진 사용자만 접근 가능
     *
     * @param orderId 취소할 주문의 시스템 식별자
     * @return 취소 완료 메시지
     * @author 최윤혁
     * @DateOfCreated 2026-01-19
     * @DateOfEdit 2026-01-19
     */
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        adminOrderService.cancelOrder(orderId);
        return ResponseEntity.ok("주문이 취소되었습니다.");
    }

    /**
     * 관리자 권한으로 주문을 삭제함
     * [보안] ROLE_ADMIN 권한을 가진 사용자만 접근 가능
     *
     * @param orderId 삭제할 주문의 시스템 식별자
     * @return 삭제 완료 메시지
     * @author 최윤혁
     * @DateOfCreated 2026-01-19
     * @DateOfEdit 2026-01-19
     */
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        adminOrderService.deleteOrder(orderId);
        return ResponseEntity.ok("주문이 삭제되었습니다.");
    }
}
