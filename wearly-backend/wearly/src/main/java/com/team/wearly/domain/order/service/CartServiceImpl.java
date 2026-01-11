package com.team.wearly.domain.order.service;


import com.team.wearly.domain.order.dto.CartRequestDto;
import com.team.wearly.domain.order.dto.CartResponseDto;
import com.team.wearly.domain.order.entity.Cart;
import com.team.wearly.domain.order.repository.CartRepository;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.repository.ProductRepository;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 장바구니 조회
    @Override
    @Transactional(readOnly = true)
    public List<CartResponseDto> getCartItems(Long userId) {
        List<Cart> carts = cartRepository.findAllByUserId(userId);
        return carts.stream()
                .map(CartResponseDto::from)
                .collect(Collectors.toList());
    }

    // 장바구니 상품 추가
    @Override
    @Transactional
    public CartResponseDto addCart(Long userId, CartRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 이미 장바구니에 있는 상품인지 확인
        Optional<Cart> existingCart = cartRepository.findByUserIdAndProductId(userId, requestDto.getProductId());

        Cart cart;
        if (existingCart.isPresent()) {
            // toBuilder()를 사용하여 기존 엔티티를 기반으로 수량만 업데이트
            cart = existingCart.get().toBuilder()
                    .quantity(existingCart.get().getQuantity() + requestDto.getQuantity())
                    .build();
        } else {
            // 없으면 새로 생성
            cart = Cart.builder()
                    .quantity(requestDto.getQuantity())
                    .user(user)
                    .product(product)
                    .build();
        }

        Cart savedCart = cartRepository.save(cart);
        return CartResponseDto.from(savedCart);
    }

    // 장바구니 상품 삭제
    @Override
    @Transactional
    public void deleteCartItem(Long userId, Long productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }

    // 장바구니 전체 비우기
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }

}
