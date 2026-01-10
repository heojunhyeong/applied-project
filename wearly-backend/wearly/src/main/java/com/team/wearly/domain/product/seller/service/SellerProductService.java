package com.team.wearly.domain.product.seller.service;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.seller.dto.request.SellerProductCreateRequest;
import com.team.wearly.domain.product.seller.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.seller.repository.SellerProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerProductService {

    // JWT 붙으면 토큰에서 뽑아오면 됨 : 임시판매자 1L
    private static final Long FIXED_SELLER_ID = 1L;

    private final SellerProductRepository sellerProductRepository;

    /** 1) 상품 등록 */
    @Transactional
    public SellerProductResponse create(SellerProductCreateRequest request) {
        Product product = Product.builder()
                .sellerId(FIXED_SELLER_ID)
                .productName(request.productName().trim())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .description(request.description().trim())
                .imageUrl(request.imageUrl().trim())
                .brand(request.brand())
                .productCategory(request.productCategory())
                .build();

        Product saved = sellerProductRepository.save(product);
        return SellerProductResponse.from(saved);
    }

    /** 2) 내 상품 목록 조회 (페이징/정렬) */
    //FIXED_SELLER_ID는 JWT 사용해 추후 수정
    public Page<SellerProductResponse> getMyProducts(Pageable pageable) {
        return sellerProductRepository.findBySellerId(FIXED_SELLER_ID, pageable)
                .map(SellerProductResponse::from);
    }

    /** 3) 내 상품 상세 */
    public SellerProductResponse getMyProduct(Long productId) {
        Product product = sellerProductRepository.findByIdAndSellerId(productId, FIXED_SELLER_ID)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return SellerProductResponse.from(product);
    }
}
