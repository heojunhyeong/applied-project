package com.team.wearly.domain.user.service;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.repository.ProductRepository;
import com.team.wearly.domain.user.dto.request.UpdateProductStatusRequest;
import com.team.wearly.domain.user.dto.response.ProductAdminResponse;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    /**
     * 시스템에 등록된 모든 상품의 목록을 조회함
     *
     * @return 관리자용 상품 정보 응답 DTO 리스트
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public List<ProductAdminResponse> getProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToProductAdminResponse)
                .toList();
    }

    /**
     * 특정 상품의 상세 규격 및 현재 상태 정보를 조회함
     *
     * @param productId 조회할 상품의 식별자
     * @return 상품 상세 정보 응답 DTO
     * @throws IllegalArgumentException 상품 식별자가 유효하지 않을 경우 발생
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public ProductAdminResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
        
        return convertToProductAdminResponse(product);
    }

    /**
     * Product 엔티티를 관리자용 응답 DTO로 변환하며 sellerName을 포함함
     *
     * @param product 상품 엔티티
     * @return 관리자용 상품 정보 응답 DTO
     */
    private ProductAdminResponse convertToProductAdminResponse(Product product) {
        Long sellerId = product.getSellerId();
        String sellerName = null;

        // Seller 정보 조회 (userName을 위해)
        if (sellerId != null) {
            Optional<Seller> seller = sellerRepository.findById(sellerId);
            sellerName = seller.map(Seller::getUserName).orElse(null);
        }

        return ProductAdminResponse.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .sellerName(sellerName)
                .productName(product.getProductName())
                .price(product.getPrice())
                .status(product.getStatus())
                .stockQuantity(product.getStockQuantity())
                .productCategory(product.getProductCategory())
                .imageUrl(product.getImageUrl())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
                .build();
    }

    /**
     * 운영 정책(금칙어 사용, 품절 처리 누락 등)에 따라 관리자 권한으로 상품의 판매 상태를 강제 수정함
     *
     * @param productId 상태를 변경할 상품의 식별자
     * @param request 새롭게 적용할 상품 상태 정보
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @Transactional
    public void updateProductStatus(Long productId, UpdateProductStatusRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
        
        product.updateStatus(request.getStatus());
    }

    /**
     * 플랫폼 운영상 부적절한 상품을 시스템에서 영구적으로 삭제함
     *
     * @param productId 삭제할 상품의 식별자
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
        
        productRepository.delete(product);
    }
}
