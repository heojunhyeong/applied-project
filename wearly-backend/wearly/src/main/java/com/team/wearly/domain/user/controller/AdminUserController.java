package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.response.UserAdminResponse;
import com.team.wearly.domain.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 관리자 전용 회원 목록 조회 및 통합 검색
     * GET /api/admin/users -> 전체 조회
     * GET /api/admin/users?keyword=검색어 -> 아이디/닉네임 검색
     */
    @GetMapping
    public ResponseEntity<List<UserAdminResponse>> getUsers(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(adminUserService.getUsers(keyword));
    }
}