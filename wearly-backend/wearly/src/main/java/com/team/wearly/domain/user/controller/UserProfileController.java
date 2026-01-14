package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.ProfileImagePresignedUrlRequest;
import com.team.wearly.domain.user.dto.request.UserProfileUpdateRequest;
import com.team.wearly.domain.user.dto.response.ProfileImagePresignedUrlResponse;
import com.team.wearly.domain.user.dto.response.UserProfileResponse;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.global.service.S3Service;
import com.team.wearly.domain.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final S3Service s3Service;

    private User getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            throw new IllegalStateException("USER 계정만 접근 가능합니다.");
        }
        return (User) principal;
    }

    // 1) 유저 프로필 조회
    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        User user = getUser(authentication);
        return ResponseEntity.ok(userProfileService.getProfile(user.getId()));
    }

    // 2) 유저 프로필 수정
    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        User user = getUser(authentication);
        return ResponseEntity.ok(userProfileService.updateProfile(user.getId(), request));
    }

    // 3) 사용자(user) 프로필 이미지 업로드를 위한 URL 생성
    @PostMapping("/presigned-url")
    public ResponseEntity<ProfileImagePresignedUrlResponse> getPresignedUrl(
            Authentication authentication,
            @Valid @RequestBody ProfileImagePresignedUrlRequest request
    ){

        //Long testUserId = 1L;  // 테스트용
        //var result = s3Service.createPresignedUrl(testUserId, request.contentType(), "users"); // 테스트용
        User user = getUser(authentication);
        var result = s3Service.createPresignedUrl(user.getId(), request.contentType(), "users");

        return ResponseEntity.ok(new ProfileImagePresignedUrlResponse(result[0], result[1]));
    }

    // 4) 프로필 이미지(URL) 등록/수정
    @PatchMapping("/image")
    public ResponseEntity<UserProfileResponse> updateProfileImage(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request
    ){
        User user = getUser(authentication);
        return ResponseEntity.ok(userProfileService.updateProfileImage(user.getId(), request.imageUrl()));
    }

}
