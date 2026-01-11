package com.team.wearly.domain.seller.controller;

import com.team.wearly.domain.seller.entity.Seller;
import com.team.wearly.domain.seller.dto.request.SellerPasswordChangeRequest;
import com.team.wearly.domain.seller.dto.request.SellerProfileUpdateRequest;
import com.team.wearly.domain.seller.dto.response.SellerProfileResponse;
import com.team.wearly.domain.seller.service.SellerPasswordService;
import com.team.wearly.domain.seller.service.SellerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/profile")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;
    private final SellerPasswordService sellerPasswordService;

    private Seller getSeller(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Seller)) {
            throw new IllegalStateException("SELLER 계정만 접근 가능합니다.");
        }
        return (Seller) principal;
    }

    // 1) 판매자 프로필 조회
    @GetMapping
    public ResponseEntity<SellerProfileResponse> getProfile(Authentication authentication) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProfileService.getProfile(seller.getId()));
    }

    // 2) 판매자 프로필 수정
    @PatchMapping
    public ResponseEntity<SellerProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody SellerProfileUpdateRequest request
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProfileService.updateProfile(seller.getId(), request));
    }

    // 3) 판매자 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody SellerPasswordChangeRequest request
    ) {
        Seller seller = getSeller(authentication);
        sellerPasswordService.changePassword(seller.getId(), request);
        return ResponseEntity.noContent().build();
    }
}
