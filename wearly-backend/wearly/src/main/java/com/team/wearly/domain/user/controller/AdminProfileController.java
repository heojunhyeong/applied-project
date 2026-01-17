package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.AdminProfileImageUpdateRequest;
import com.team.wearly.domain.user.dto.request.AdminProfileUpdateRequest;
import com.team.wearly.domain.user.dto.request.ProfileImagePresignedUrlRequest;
import com.team.wearly.domain.user.dto.response.AdminProfileResponse;
import com.team.wearly.domain.user.dto.response.ProfileImagePresignedUrlResponse;
import com.team.wearly.domain.user.entity.Admin;
import com.team.wearly.domain.user.service.AdminProfileService;
import com.team.wearly.global.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/profile")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;
    private final S3Service s3Service;

    // SecurityContext에서 현재 인증된 관리자(Admin) 객체를 추출하는 메소드
    private Admin getAdmin(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Admin)) {
            throw new IllegalStateException("ADMIN 계정만 접근 가능합니다.");
        }
        return (Admin) principal;
    }

    // 관리자 프로필 조회 메소드
    @GetMapping
    public ResponseEntity<AdminProfileResponse> getProfile(Authentication authentication) {
        Admin admin = getAdmin(authentication);
        return ResponseEntity.ok(adminProfileService.getProfile(admin.getId()));
    }

    // 관리자 프로필 수정 메소드
    @PatchMapping
    public ResponseEntity<AdminProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody AdminProfileUpdateRequest request
    ) {
        Admin admin = getAdmin(authentication);
        return ResponseEntity.ok(adminProfileService.updateProfile(admin.getId(), request));
    }

    // 관리자 프로필 이미지 업로드 Presigned URL 발급 메소드
    @PostMapping("/presigned-url")
    public ResponseEntity<ProfileImagePresignedUrlResponse> getPresignedUrl(
            Authentication authentication,
            @Valid @RequestBody ProfileImagePresignedUrlRequest request
    ) {
        Admin admin = getAdmin(authentication);

        // S3 Presigned URL 생성 메소드
        var result = s3Service.createPresignedUrl(admin.getId(), request.contentType(), "admin");

        return ResponseEntity.ok(new ProfileImagePresignedUrlResponse(result[0], result[1]));
    }

    // S3 업로드 완료 후 관리자 프로필 이미지 URL 반영 메소드
    @PatchMapping("/image")
    public ResponseEntity<AdminProfileResponse> updateProfileImage(
            Authentication authentication,
            @Valid @RequestBody AdminProfileImageUpdateRequest request
    ) {
        Admin admin = getAdmin(authentication);
        return ResponseEntity.ok(adminProfileService.updateProfileImage(admin.getId(), request.imageUrl()));
    }
}
