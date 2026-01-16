package com.team.wearly.domain.seller.controller;

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
     * * @param authentication 인증 정보 객체
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
     * 현재 로그인한 판매자의 브랜드명, 담당자 정보 등 프로필 상세 내역을 조회함
     *
     * @param authentication 인증 정보 객체
     * @return 판매자 프로필 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @GetMapping
    public ResponseEntity<SellerProfileResponse> getProfile(Authentication authentication) {
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProfileService.getProfile(seller.getId()));
    }


    /**
     * 판매자의 프로필 정보(브랜드 설명, 연락처 등)를 수정함
     *
     * @param authentication 인증 정보 객체
     * @param request 수정할 프로필 정보 DTO
     * @return 수정된 프로필 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
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
     * 판매자의 계정 비밀번호를 안전하게 변경함
     *
     * @param authentication 인증 정보 객체
     * @param request 기존 비밀번호 및 신규 비밀번호가 포함된 DTO
     * @return 성공 시 204 No Content
     * @author 허보미
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
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
     * 프로필 이미지 업로드를 위해 AWS S3로부터 Presigned URL과 이미지 경로를 생성받음
     *
     * @param authentication 인증 정보 객체
     * @param request 이미지의 Content-Type 정보
     * @return 업로드용 URL과 파일 경로 정보 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @PostMapping("/presigned-url")
    public ResponseEntity<ProfileImagePresignedUrlResponse> getPresignedUrl(
            Authentication authentication,
            @Valid @RequestBody ProfileImagePresignedUrlRequest request
    ){
        //Long testSellerId = 1L; // 테스트용
        //var result = s3Service.createPresignedUrl(testSellerId, request.contentType(), "seller"); //테스트용
        Seller seller = getSeller(authentication);
        var result = s3Service.createPresignedUrl(seller.getId(), request.contentType(), "seller");

        return ResponseEntity.ok(new ProfileImagePresignedUrlResponse(result[0], result[1]));
    }

    /**
     * S3 업로드가 완료된 이미지의 공개 URL을 판매자 프로필 정보에 반영함
     *
     * @param authentication 인증 정보 객체
     * @param request 이미지 URL 정보가 포함된 DTO
     * @return 업데이트된 프로필 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @PatchMapping("/image")
    public ResponseEntity<SellerProfileResponse> updateProfileImage(
            Authentication authentication,
            @Valid @RequestBody SellerProfileUpdateRequest request
    ){
        Seller seller = getSeller(authentication);
        return ResponseEntity.ok(sellerProfileService.updateProfileImage(seller.getId(), request.imageUrl()));
    }
}
