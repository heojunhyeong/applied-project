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

    /**
     * SecurityContext의 인증 정보로부터 현재 로그인한 일반 사용자(User) 객체를 추출함
     *
     * @param authentication 인증 정보 객체
     * @return User 엔티티 객체
     * @throws IllegalStateException USER 권한이 아닌 계정이 접근할 경우 발생
     * @author 정찬혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    private User getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            throw new IllegalStateException("USER 계정만 접근 가능합니다.");
        }
        return (User) principal;
    }


    /**
     * 사용자의 닉네임, 이메일, 프로필 이미지 경로 등 상세 프로필 정보를 조회함
     *
     * @param authentication 인증 정보 객체
     * @return 사용자 프로필 응답 DTO
     * @author 정찬혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        User user = getUser(authentication);
        return ResponseEntity.ok(userProfileService.getProfile(user.getId()));
    }


    /**
     * 사용자의 닉네임이나 기본 정보를 수정함
     *
     * @param authentication 인증 정보 객체
     * @param request 수정할 프로필 정보가 담긴 DTO
     * @return 수정 완료된 프로필 응답 DTO
     * @author 정찬혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        User user = getUser(authentication);
        return ResponseEntity.ok(userProfileService.updateProfile(user.getId(), request));
    }


    /**
     * S3 서버에 프로필 이미지를 직접 업로드할 수 있도록 유효시간이 포함된 임시 업로드 URL을 생성함
     *
     * @param authentication 인증 정보 객체
     * @param request 업로드할 이미지의 파일 형식(Content-Type) 정보
     * @return S3 Presigned URL 및 파일 저장 경로 정보
     * @author 정찬혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
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

    /**
     * 클라이언트에서 S3 업로드를 마친 후, 해당 이미지의 접근 가능 URL을 사용자의 프로필 정보에 반영함
     *
     * @param authentication 인증 정보 객체
     * @param request 이미지 URL 정보를 포함한 DTO
     * @return 최종 업데이트된 프로필 정보
     * @author 정찬혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @PatchMapping("/image")
    public ResponseEntity<UserProfileResponse> updateProfileImage(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request
    ){
        User user = getUser(authentication);
        return ResponseEntity.ok(userProfileService.updateProfileImage(user.getId(), request.imageUrl()));
    }
}
