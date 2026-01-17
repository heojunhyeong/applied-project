package com.team.wearly.domain.order.controller;

import com.team.wearly.domain.order.dto.response.OrderDetailResponse;
import com.team.wearly.domain.order.dto.response.OrderHistoryResponse;
import com.team.wearly.domain.order.dto.response.OrderSheetResponse;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;
import com.team.wearly.domain.order.service.OrderService;
import com.team.wearly.domain.product.entity.enums.Size;
import com.team.wearly.domain.user.entity.Admin;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof Seller) {
            return ((Seller) principal).getId();
        } else {
            throw new IllegalStateException("지원하지 않는 사용자 타입입니다.");
        }
    }

    /**
     * 프론트엔드에서 주문 번호를 알려주는 API
     *
     * @param request 주문 상품의 정보
     * @return 주문이 성공하였을때 성공
     * @author 허준형
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2025-01-11
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(
            Authentication authentication,
            @RequestBody OrderCreateRequest request) {

        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(orderService.createOrder(userId, request));

        // 테스트용, 위 주석으로 교체 필요
        // Long userId;
        // if (authentication == null || authentication.getName() == null) {
        // userId = 2L; // DB에 넣은 테스트 유저 ID
        // } else {
        // try {
        // userId = Long.parseLong(authentication.getName());
        // } catch (NumberFormatException e) {
        // userId = 2L; // 형식이 맞지 않을 경우 대비
        // }
        // }
        // return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    /**
     * 현재 로그인한 사용자의 전체 주문 내역을 조회하는 API
     *
     * @param authentication 인증된 사용자의 정보 (userId 추출용)
     * @return 사용자의 주문 이력 리스트
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @GetMapping
    public ResponseEntity<List<OrderHistoryResponse>> getMyOrders(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    /**
     * 특정 주문 번호에 대한 상세 정보를 조회하는 API
     *
     * @param orderId 조회를 원하는 주문 번호
     * @return 주문 상세 정보 데이터
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    /**
     * 주문 상세 검색 API (상품명 키워드 검색)
     * GET /api/users/orders/search?keyword=감자
     * 키워드가 포함된 상품과 같은 날짜에 주문된 모든 상품을 반환합니다.
     * 예: "감자"로 검색 시, 감자가 포함된 상품과 같은 날 주문된 고구마 등도 함께 반환됩니다.
     *
     * @param authentication 인증 정보 (사용자 ID 추출용)
     * @param keyword        상품명 검색 키워드
     * @return 주문 상세 상품 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<OrderDetailResponse.OrderItemDto>> searchOrderDetails(
            Authentication authentication,
            @RequestParam(required = false) String keyword) {

        Long userId = getUserIdFromAuthentication(authentication);
        List<OrderDetailResponse.OrderItemDto> response = orderService.searchOrderDetailsByKeyword(userId, keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자가 상품 수령 후 구매를 최종 확정하는 API
     *
     * @param userId        인증된 사용자의 식별자
     * @param orderId       해당 상품이 포함된 주문 번호
     * @param orderDetailId 확정할 개별 상품의 상세 번호
     * @return 구매 확정 성공 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @PostMapping("/{orderId}/details/{orderDetailId}/confirm")
    public ResponseEntity<String> confirmPurchase(
            @AuthenticationPrincipal Long userId,
            @PathVariable String orderId,
            @PathVariable Long orderDetailId) {

        orderService.confirmPurchase(userId, orderId, orderDetailId);
        return ResponseEntity.ok("구매 확정이 완료되었습니다. 정산 프로세스가 시작됩니다.");
    }

    @GetMapping("/sheet")
    public ResponseEntity<OrderSheetResponse> getOrderSheet(
            @RequestParam(required = false) List<Long> cartItemIds,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long quantity,
            @RequestParam(required = false) Size size,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        OrderSheetResponse response = orderService.getOrderSheet(userId, cartItemIds, productId, quantity, size);
        return ResponseEntity.ok(response);
    }
}
