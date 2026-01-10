package com.team.wearly.domain.user.seller.controller;

import com.team.wearly.domain.user.seller.dto.request.SellerPasswordChangeRequest;
import com.team.wearly.domain.user.seller.dto.request.SellerProfileUpdateRequest;
import com.team.wearly.domain.user.seller.dto.response.SellerProfileResponse;
import com.team.wearly.domain.user.seller.service.SellerPasswordService;
import com.team.wearly.domain.user.seller.service.SellerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/profile")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;
    private final SellerPasswordService sellerPasswordService;

    // 1) 판매자 프로필 조회
    @GetMapping
    public ResponseEntity<SellerProfileResponse> getProfile() {
        return ResponseEntity.ok(sellerProfileService.getProfile());
    }

    // 2) 판매자 프로필 수정 (닉네임/소개/연락처)
    @PatchMapping
    public ResponseEntity<SellerProfileResponse> updateProfile(
            @Valid @RequestBody SellerProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(sellerProfileService.updateProfile(request));
    }

    // 3) 판매자 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody SellerPasswordChangeRequest request
    ) {
        sellerPasswordService.changePassword(request);
        return ResponseEntity.noContent().build(); // 204
    }
}
