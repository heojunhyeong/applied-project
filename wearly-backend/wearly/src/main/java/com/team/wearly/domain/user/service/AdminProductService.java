package com.team.wearly.domain.user.service;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.repository.ProductRepository;
import com.team.wearly.domain.user.dto.request.UpdateProductStatusRequest;
import com.team.wearly.domain.user.dto.response.ProductAdminResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

    private final ProductRepository productRepository;

    /** 관리자용 상품 목록 조회 */
    public List<ProductAdminResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductAdminResponse::from)
                .toList();
    }

    /** 관리자용 상품 판매 상태 수정 */
    @Transactional
    public void updateProductStatus(Long productId, UpdateProductStatusRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        // Product 엔티티에서 상태 변경 메서드 이름이 바뀜
        product.changeStatus(request.getStatus());
    }

    /** 관리자용 상품 삭제 (물리 삭제) */
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        productRepository.delete(product);
    }
}
