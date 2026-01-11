package com.team.wearly.domain.order.controller;


import com.team.wearly.domain.order.dto.CartRequestDto;
import com.team.wearly.domain.order.dto.CartResponseDto;
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

    // 장바구니 조회
    @GetMapping("/items")
    public ResponseEntity<List<CartResponseDto>> getCartItems(
            Authentication authentication)
    {
        Long userId = Long.parseLong(authentication.getName());
        List<CartResponseDto> response = cartService.getCartItems(userId);
        return ResponseEntity.ok(response);
    }

    // 장바구니 상품 추가
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addCart(
            Authentication authentication,
            @Valid @RequestBody CartRequestDto requestDto)
    {
        Long userId = Long.parseLong(authentication.getName());
        CartResponseDto response = cartService.addCart(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 개별 상품 삭제
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(
            Authentication authentication,
            @PathVariable Long productId)
    {
        Long userId = Long.parseLong(authentication.getName());
        cartService.deleteCartItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 전체 비우기
    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(Authentication authentication)
    {
        Long userId = Long.parseLong(authentication.getName());
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
