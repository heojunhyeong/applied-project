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


    /**
     * 새로운 상품을 등록하며 초기 상태를 '판매중(ON_SALE)'으로 설정함
     *
     * @param sellerId 판매자 식별자
     * @param request 상품명, 가격, 재고, 이미지 등 등록 정보
     * @return 등록된 상품 상세 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
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

    /**
     * 판매자 본인이 등록한 상품 중 삭제되지 않은 상품 목록을 페이징하여 조회함
     *
     * @param sellerId 판매자 식별자
     * @param pageable 페이징 정보
     * @return 판매자의 상품 응답 DTO 페이지
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public Page<SellerProductResponse> getMyProducts(Long sellerId, Pageable pageable) {
        return sellerProductRepository
                .findBySellerIdAndStatusIn(sellerId, VISIBLE_STATUSES, pageable)
                .map(SellerProductResponse::from);
    }

    /**
     * 특정 상품의 상세 정보를 조회하며, 판매자 본인의 상품이거나 삭제된 상태가 아님을 검증함
     *
     * @param sellerId 판매자 식별자
     * @param productId 상품 식별자
     * @return 상품 상세 정보 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public SellerProductResponse getMyProduct(Long sellerId, Long productId) {
        Product product = sellerProductRepository
                .findByIdAndSellerIdAndStatusIn(productId, sellerId, VISIBLE_STATUSES)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return SellerProductResponse.from(product);
    }

    /**
     * 기존 상품 정보를 전달받은 요청 데이터로 전체 업데이트함 (재고, 상태, 사이즈 포함)
     *
     * @param sellerId 판매자 식별자
     * @param productId 수정할 상품 식별자
     * @param request 수정할 상품 정보 객체
     * @return 수정 완료된 상품 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
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


    /**
     * 상품을 실제로 DB에서 삭제하지 않고 상태를 'DELETED'로 변경하여 목록 및 상세 노출을 차단함
     *
     * @param sellerId 판매자 식별자
     * @param productId 삭제할 상품 식별자
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @Transactional
    public void deleteMyProduct(Long sellerId, Long productId) {
        Product product = sellerProductRepository
                .findByIdAndSellerIdAndStatusIn(productId, sellerId, VISIBLE_STATUSES)
                .orElseThrow(() -> new IllegalArgumentException("내 상품이 아니거나 상품이 존재하지 않습니다."));

        product.updateStatus(ProductStatus.DELETED);
    }
}
