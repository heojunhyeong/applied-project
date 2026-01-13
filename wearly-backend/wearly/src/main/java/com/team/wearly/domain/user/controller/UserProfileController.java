package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.UserProfileUpdateRequest;
import com.team.wearly.domain.user.dto.response.UserProfileResponse;
import com.team.wearly.domain.user.entity.User;
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

    //프로필 페이지 이미지 변경 시작할게요~!~!~
}
