package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.PasswordResetConfirmDto;
import com.team.wearly.domain.user.dto.request.PasswordResetRequestDto;
import com.team.wearly.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordController {

    private final UserService userService;

    // 재설정 메일 요청
    @PostMapping("/reset/request")
    public ResponseEntity<Void> requestReset(@RequestBody PasswordResetRequestDto dto) {
        userService.requestPasswordReset(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    // 실제 비밀번호 변경
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetConfirmDto dto) {
        userService.resetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }
}