package com.team.wearly.domain.seller.controller;

import com.team.wearly.domain.seller.dto.request.SellerProfileImageUpdateRequest; // ✅ 추가
import com.team.wearly.domain.user.dto.request.ProfileImagePresignedUrlRequest;
import com.team.wearly.domain.user.dto.response.ProfileImagePresignedUrlResponse;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.seller.dto.request.SellerPasswordChangeRequest;
import com.team.wearly.domain.seller.dto.request.SellerProfileUpdateRequest;
import com.team.wearly.domain.seller.dto.response.SellerProfileResponse;
import com.team.wearly.domain.seller.service.SellerPasswordService;
import com.team.wearly.domain.seller.service.SellerProfileService;
import com.team.wearly.global.service.S3Service;
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
    private final S3Service s3Service;

    /**
     * SecurityContext에서 현재 인증된 판매자(Seller) 객체를 추출함
     * @param authentication 인증 정보 객체
     * @return Seller 엔티티 객체
     * @throws IllegalStateException 판매자 권한이 아닐 경우 발생
     * @author 허보미
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    private Seller getSeller(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Seller)) {
            throw new IllegalStateException("SELLER 계정만 접근 가능합니다.");
        }
        return (Seller) principal;
    }

    /**
     * 판매자 프로필 조회 메소드
     */
    @GetMapping
    public ResponseEntity<SellerProfileResponse> getProfile(Authentication authentication) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProfileService.getProfile(seller.getId()));
    }

    /**
     * 판매자 프로필 수정 메소드
     */
    @PatchMapping
    public ResponseEntity<SellerProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody SellerProfileUpdateRequest request
    ) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProfileService.updateProfile(seller.getId(), request));
    }

    /**
     * 판매자 비밀번호 변경 메소드
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody SellerPasswordChangeRequest request
    ) {
        Seller seller = getSeller(authentication);
        sellerPasswordService.changePassword(seller.getId(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 판매자 프로필 Presigned URL 생성 메소드
     */
    @PostMapping("/presigned-url")
    public ResponseEntity<ProfileImagePresignedUrlResponse> getPresignedUrl(
            Authentication authentication,
            @Valid @RequestBody ProfileImagePresignedUrlRequest request
    ){
        Seller seller = getSeller(authentication);
        var result = s3Service.createPresignedUrl(seller.getId(), request.contentType(), "seller");
        return ResponseEntity.ok(new ProfileImagePresignedUrlResponse(result[0], result[1]));
    }

    /**
     * 판매자 프로필 이미지 URL 반영 메소드
     * - ✅ imageUrl만 받는 DTO로 분리 (닉네임 NotBlank 때문에 400 나는 것 방지)
     */
    @PatchMapping("/image")
    public ResponseEntity<SellerProfileResponse> updateProfileImage(
            Authentication authentication,
            @Valid @RequestBody SellerProfileImageUpdateRequest request
    ){
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(
                sellerProfileService.updateProfileImage(seller.getId(), request.imageUrl())
        );
    }
}
