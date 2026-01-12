package com.team.wearly.domain.product.controller;

import com.team.wearly.domain.product.dto.request.SellerProductUpsertRequest;
import com.team.wearly.domain.product.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.service.SellerProductService;
import com.team.wearly.domain.user.entity.Seller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
public class SellerProductController {

    private final SellerProductService sellerProductService;

    private Seller getSeller(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Seller)) {
            throw new IllegalStateException("SELLER 계정만 접근 가능합니다.");
        }
        return (Seller) principal;
    }

    /** 1) 상품 등록 */
    @PostMapping
    public ResponseEntity<SellerProductResponse> create(
            Authentication authentication,
            @Valid @RequestBody SellerProductUpsertRequest request
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProductService.create(seller.getId(), request));
    }

    /** 2) 내 상품 목록 조회 (페이징/정렬) */
    @GetMapping
    public ResponseEntity<Page<SellerProductResponse>> getMyProducts(
            Authentication authentication,
            Pageable pageable
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProductService.getMyProducts(seller.getId(), pageable));
    }

    /** 3) 내 상품 상세 */
    @GetMapping("/{productId}")
    public ResponseEntity<SellerProductResponse> getMyProduct(
            Authentication authentication,
            @PathVariable Long productId
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProductService.getMyProduct(seller.getId(), productId));
    }

    /** 4) 내 상품 수정 (전체 수정 / PUT) */
    @PutMapping("/{productId}")
    public ResponseEntity<SellerProductResponse> updateMyProduct(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody SellerProductUpsertRequest request
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProductService.updateMyProduct(seller.getId(), productId, request));
    }

    /** 5) 내 상품 삭제 (소프트 삭제로 status 변경) */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteMyProduct(
            Authentication authentication,
            @PathVariable Long productId
    ) {
        Seller seller = getSeller(authentication);
        sellerProductService.deleteMyProduct(seller.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}
