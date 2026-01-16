package com.team.wearly.domain.order.service;


import com.team.wearly.domain.order.dto.request.CartRequestDto;
import com.team.wearly.domain.order.dto.response.CartResponseDto;
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

    /**
     * 특정 사용자의 장바구니에 담긴 모든 아이템을 조회하여 DTO 리스트로 변환함
     *
     * @param userId 장바구니를 조회할 사용자의 식별자
     * @return 장바구니 상품 정보 DTO 리스트
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartResponseDto> getCartItems(Long userId) {
        List<Cart> carts = cartRepository.findAllByUserId(userId);
        return carts.stream()
                .map(CartResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 장바구니에 상품을 추가하며, 이미 존재하는 상품일 경우 수량을 합산하여 갱신함
     *
     * @param userId 상품을 담는 사용자의 식별자
     * @param requestDto 추가할 상품의 ID와 수량 정보
     * @return 저장되거나 수정된 장바구니 아이템 정보
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Override
    @Transactional
    public CartResponseDto addCart(Long userId, CartRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 사이즈 유효성 검증
        if (!product.getAvailableSizes().contains(requestDto.getSize())) {
            throw new IllegalArgumentException("해당 상품에 존재하지 않는 사이즈입니다: " + requestDto.getSize());
        }

        // 이미 장바구니에 있는 상품인지 확인 (상품 ID + 사이즈까지 체크해야 함!)
        Optional<Cart> existingCart = cartRepository.findByUserIdAndProductIdAndSize(
                userId,
                requestDto.getProductId(),
                requestDto.getSize()
        );

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
                    .size(requestDto.getSize())
                    .build();
        }

        Cart savedCart = cartRepository.save(cart);
        return CartResponseDto.from(savedCart);
    }

    /**
     * 특정 사용자의 장바구니에서 지정된 상품을 제거함
     *
     * @param userId 상품을 삭제할 사용자의 식별자
     * @param productId 삭제할 상품의 식별자
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Override
    @Transactional
    public void deleteCartItem(Long userId, Long productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * 주문 완료 후 혹은 사용자의 요청에 의해 장바구니의 모든 내역을 삭제함
     *
     * @param userId 장바구니를 비울 사용자의 식별자
     * @author 정찬혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }

}
