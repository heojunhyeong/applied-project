package com.team.wearly.domain.product.seller.controller;

import com.team.wearly.domain.product.seller.dto.request.SellerProductCreateRequest;
import com.team.wearly.domain.product.seller.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.seller.service.SellerProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
public class SellerProductController {

    private final SellerProductService sellerProductService;

    //상품 등록
    @PostMapping
    public ResponseEntity<SellerProductResponse> create(@Valid @RequestBody SellerProductCreateRequest request) {
        return ResponseEntity.ok(sellerProductService.create(request));
    }

    //내 상품 목록 (페이징/정렬)
    @GetMapping
    public ResponseEntity<Page<SellerProductResponse>> getMyProducts(Pageable pageable) {
        return ResponseEntity.ok(sellerProductService.getMyProducts(pageable));
    }

    //내 상품 상세
    @GetMapping("/{productId}")
    public ResponseEntity<SellerProductResponse> getMyProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(sellerProductService.getMyProduct(productId));
    }
}
