package com.team.wearly.domain.order.service;

import com.team.wearly.domain.order.dto.request.CartRequestDto;
import com.team.wearly.domain.order.dto.response.CartResponseDto;

import java.util.List;

public interface CartService {
    // 장바구니 조회
    List<CartResponseDto> getCartItems(Long userId);

    // 장바구니 상품 추가
    CartResponseDto addCart(Long userId, CartRequestDto requestDto);

    // 장바구니 상품 삭제
    void deleteCartItem(Long userId, Long cartId);

    // 장바구니 전체 비우기
    void clearCart(Long userId);
}
