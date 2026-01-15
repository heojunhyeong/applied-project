package com.team.wearly.domain.product.controller;

import com.team.wearly.domain.product.dto.request.ProductSearchCondition;
import com.team.wearly.domain.product.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.service.ProductService;
import com.team.wearly.domain.review.dto.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<SellerProductResponse>> searchProducts(
            @ModelAttribute ProductSearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.searchProducts(condition, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategory>> getCategoriesByBrand(@RequestParam Brand brand) {
        return ResponseEntity.ok(productService.getCategoriesByBrand(brand));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<SellerProductResponse> getProductDetail(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductDetail(productId));
    }

    //상품 리뷰 목록 조회 (페이징)
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getProductReviews(productId, pageable));
    }
}