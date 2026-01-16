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
     * 플랫폼에 등록된 모든 상품의 목록과 핵심 정보를 조회함
     * [보안] ROLE_ADMIN 권한을 가진 관리자만 호출 가능
     *
     * @return 상품 관리용 응답 DTO 리스트
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ProductAdminResponse>> getProducts() {
        List<ProductAdminResponse> response = adminProductService.getProducts();
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 상품의 상세 규격, 재고 상태 및 판매자 정보를 포함한 전체 데이터를 조회함
     * [보안] ROLE_ADMIN 권한 필수
     *
     * @param productId 조회 대상 상품의 식별자
     * @return 관리자용 상품 상세 정보 DTO
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductAdminResponse> getProduct(@PathVariable Long productId) {
        ProductAdminResponse response = adminProductService.getProduct(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * 운영 정책 위반 또는 재고 관리 등의 사유로 관리자가 상품의 판매 상태를 강제 수정함
     *
     * @param productId 상태를 변경할 상품의 식별자
     * @param request 변경하고자 하는 상품 상태값(ON_SALE, SOLD_OUT 등) 정보
     * @return 성공 시 200 OK
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
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
     * 플랫폼 운영상 부적절한 상품을 시스템에서 영구적 또는 소프트 삭제 처리함
     *
     * @param productId 삭제할 상품의 식별자
     * @return 성공 시 204 No Content
     * @author 최윤혁
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
