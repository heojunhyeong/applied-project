package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.UpdateProductStatusRequest;
import com.team.wearly.domain.user.dto.response.ProductAdminResponse;
import com.team.wearly.domain.user.service.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * 관리자용 상품 목록 조회 API
     * GET /api/admin/products
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ProductAdminResponse>> getProducts() {
        List<ProductAdminResponse> response = adminProductService.getProducts();
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자용 상품 상세 조회 API
     * GET /api/admin/products/{productId}
     */
    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductAdminResponse> getProduct(@PathVariable Long productId) {
        ProductAdminResponse response = adminProductService.getProduct(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자용 상품 판매 상태 수정 API
     * PUT /api/admin/products/{productId}/status
     */
    @PutMapping("/{productId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProductStatus(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductStatusRequest request) {
        adminProductService.updateProductStatus(productId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 관리자용 상품 삭제 API
     * DELETE /api/admin/products/{productId}
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
