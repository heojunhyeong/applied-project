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

    /**
     * 사용자의 이메일로 비밀번호 재설정 링크(인증 토큰 포함)를 포함한 메일을 발송함
     *
     * @param dto 재설정 링크를 받을 사용자의 이메일 정보
     * @return 성공 시 200 OK
     * @author 허준형
     * @DateOfCreated 2026-01-16
     * @DateOfEdit 2026-01-16
     */
    @PostMapping("/reset/request")
    public ResponseEntity<Void> requestReset(@RequestBody PasswordResetRequestDto dto) {
        userService.requestPasswordReset(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * 메일로 발송된 인증 토큰의 유효성을 검증한 후, 사용자의 비밀번호를 새로운 비밀번호로 변경함
     *
     * @param dto 인증 토큰 및 새로 설정할 비밀번호 정보
     * @return 성공 시 200 OK
     * @author 허준형
     * @DateOfCreated 2026-01-16
     * @DateOfEdit 2026-01-16
     */
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetConfirmDto dto) {
        userService.resetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }
}