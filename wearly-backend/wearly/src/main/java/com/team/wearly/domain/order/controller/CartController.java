package com.team.wearly.domain.order.controller;


import com.team.wearly.domain.order.dto.request.CartRequestDto;
import com.team.wearly.domain.order.dto.response.CartResponseDto;
import com.team.wearly.domain.order.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/cart")
public class CartController {
    private final CartService cartService;

    /**
     * 현재 로그인한 사용자의 장바구니에 담긴 전체 상품 목록을 조회하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @return 장바구니 상품 리스트 (CartResponseDto)
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @GetMapping("/items")
    public ResponseEntity<List<CartResponseDto>> getCartItems(
            Authentication authentication)
    {
        Long userId = Long.parseLong(authentication.getName());
        List<CartResponseDto> response = cartService.getCartItems(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 장바구니에 새로운 상품을 추가하거나 기존 상품의 수량을 변경하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @param requestDto 추가할 상품 정보 및 수량
     * @return 추가된 장바구니 상품 정보
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addCart(
            Authentication authentication,
            @Valid @RequestBody CartRequestDto requestDto)
    {
        Long userId = Long.parseLong(authentication.getName());
        CartResponseDto response = cartService.addCart(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 장바구니에서 특정 상품 하나를 선택하여 삭제하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @param productId 삭제하려는 상품의 식별자
     * @return 성공 시 컨텐츠 없음(204 No Content) 반환
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(
            Authentication authentication,
            @PathVariable Long productId)
    {
        Long userId = Long.parseLong(authentication.getName());
        cartService.deleteCartItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자의 장바구니에 담긴 모든 상품을 일괄 삭제하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @return 성공 시 컨텐츠 없음(204 No Content) 반환
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(Authentication authentication)
    {
        Long userId = Long.parseLong(authentication.getName());
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
