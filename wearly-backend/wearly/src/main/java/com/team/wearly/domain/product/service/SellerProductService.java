package com.team.wearly.domain.product.service;

import com.team.wearly.domain.product.dto.request.SellerProductUpsertRequest;
import com.team.wearly.domain.product.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import com.team.wearly.domain.product.repository.SellerProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerProductService {

    private final SellerProductRepository sellerProductRepository;

    // 목록/상세 조회 시: "노출되는 상품"만
    private static final List<ProductStatus> VISIBLE_STATUSES =
            List.of(ProductStatus.ON_SALE, ProductStatus.SOLD_OUT);

    /** 1) 상품 등록 */
    @Transactional
    public SellerProductResponse create(Long sellerId, SellerProductUpsertRequest request) {
        Product product = Product.builder()
                .sellerId(sellerId)
                .productName(request.productName().trim())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .description(request.description().trim())
                .imageUrl(request.imageUrl().trim())
                .brand(request.brand())
                .productCategory(request.productCategory())
                .status(ProductStatus.ON_SALE) // 등록 기본값
                .availableSizes(new HashSet<>(request.sizes())) // [수정] 리스트를 Set으로 변환 저장
                .build();

        Product saved = sellerProductRepository.save(product);
        return SellerProductResponse.from(saved);
    }

    /** 2) 내 상품 목록 조회 (DELETED 제외) */
    public Page<SellerProductResponse> getMyProducts(Long sellerId, Pageable pageable) {
        return sellerProductRepository
                .findBySellerIdAndStatusIn(sellerId, VISIBLE_STATUSES, pageable)
                .map(SellerProductResponse::from);
    }

    /** 3) 내 상품 상세 (DELETED 제외) */
    public SellerProductResponse getMyProduct(Long sellerId, Long productId) {
        Product product = sellerProductRepository
                .findByIdAndSellerIdAndStatusIn(productId, sellerId, VISIBLE_STATUSES)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return SellerProductResponse.from(product);
    }

    /** 4) 내 상품 수정 (전체 수정 / PUT) */
    @Transactional
    public SellerProductResponse updateMyProduct(Long sellerId, Long productId, SellerProductUpsertRequest request) {
        Product product = sellerProductRepository
                .findByIdAndSellerIdAndStatusIn(productId, sellerId, VISIBLE_STATUSES)
                .orElseThrow(() -> new IllegalArgumentException("내 상품이 아니거나 상품이 존재하지 않습니다."));

        product.update(
                request.productName().trim(),
                request.price(),
                request.stockQuantity(),
                request.description().trim(),
                request.imageUrl().trim(),
                request.brand(),
                request.productCategory(),
                request.status(),
                new HashSet<>(request.sizes()) //사이즈 목록 업데이트
        );

        return SellerProductResponse.from(product);
    }

    /** 5) 내 상품 삭제 (결제/배송 상태 상관없이 소프트 삭제) */
    /** 삭제 시 목록/상세/수정 에서 조회 자체가 안됨*/
    /** 추후 환불/취소 정책은 주문 도메인에서 처리*/
    @Transactional
    public void deleteMyProduct(Long sellerId, Long productId) {
        Product product = sellerProductRepository
                .findByIdAndSellerIdAndStatusIn(productId, sellerId, VISIBLE_STATUSES)
                .orElseThrow(() -> new IllegalArgumentException("내 상품이 아니거나 상품이 존재하지 않습니다."));

        product.updateStatus(ProductStatus.DELETED);
    }
}
