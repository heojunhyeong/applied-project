package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.LoginRequest;
import com.team.wearly.domain.user.dto.response.ErrorResponse;
import com.team.wearly.domain.user.dto.response.LoginResponse;
import com.team.wearly.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    /**
     * 이메일과 비밀번호를 검증하여 JWT 액세스 토큰 및 회원 정보를 발급함
     * 로직 내부적으로 회원 타입(Role)을 판별하여 결과값을 반환함
     *
     * @param request 이메일, 비밀번호, (선택 시)회원 타입 정보가 포함된 로그인 요청 DTO
     * @return 성공 시 200 OK와 함께 토큰/정보 반환, 실패 시 해당 에러 코드 및 메시지 반환
     * @author 최윤혁
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2026-01-11
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
