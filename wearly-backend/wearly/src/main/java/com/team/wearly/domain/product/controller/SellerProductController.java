package com.team.wearly.domain.product.controller;

import com.team.wearly.domain.product.dto.request.SellerProductUpsertRequest;
import com.team.wearly.domain.product.dto.response.SellerProductResponse;
import com.team.wearly.domain.product.service.SellerProductService;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.global.service.S3Service;
import com.team.wearly.global.util.PresignedUrlVo;
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
    private final S3Service s3Service;

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

    /**
     * 5) 내 상품 삭제 (소프트 삭제)
     * - 결제/배송 상태와 무관하게 소프트 삭제 처리 (status=DELETED)
     * - 삭제된 상품은 내 상품 목록/상세/수정에서 조회되지 않음
     * - 추후 환불/취소 정책은 주문/결제 도메인에서 별도 처리 예정
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteMyProduct(
            Authentication authentication,
            @PathVariable Long productId
    ) {
        Seller seller = getSeller(authentication);
        sellerProductService.deleteMyProduct(seller.getId(), productId);
        return ResponseEntity.noContent().build();
    }

    //Presigned URL 발급 API 메서드 추가
    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlVo> getPresignedUrl(
            @RequestParam String extension,
            @RequestParam String type
    ) {
        String folderPath;

        if ("THUMBNAIL".equalsIgnoreCase(type)) {
            folderPath = "products/thumbnail";
        } else if ("DESCRIPTION".equalsIgnoreCase(type)) {
            folderPath = "products/description";
        } else {
            throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다: " + type);
        }

        // [수정됨] 서비스가 String[]을 반환하므로 배열로 받음
        String[] result = s3Service.createProductPresignedUrl(folderPath, extension);

        // 결과의 0번째는 URL, 1번째는 Key
        return ResponseEntity.ok(new PresignedUrlVo(result[0], result[1]));
    }
}
