package com.team.wearly.domain.order.service;

import com.team.wearly.domain.order.dto.CartRequestDto;
import com.team.wearly.domain.order.dto.CartResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    // 장바구니 조회
    public List<CartResponseDto> getCartItems(Long userId){
        return null;
    }

    // 장바구니 상품 추가
    public CartResponseDto addCart(Long userId, CartRequestDto requestDto){
        return null;
    }

    // 장바구니 상품 삭제
    public void deleteCartItem(Long userId, Long productId){

    }

    // 장바구니 전체 비우기
    public void clearCart(Long userId){

    }
}
