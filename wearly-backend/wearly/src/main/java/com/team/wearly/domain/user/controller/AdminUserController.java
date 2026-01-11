package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.response.UserAdminResponse;
import com.team.wearly.domain.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 관리자용 회원 목록 조회 API
     * * [사용 예시]
     * 1. 전체 조회: GET /api/admin/users
     * 2. 검색 조회: GET /api/admin/users?keyword=~~~
     */
    @GetMapping
    public ResponseEntity<List<UserAdminResponse>> getUsers(
            @RequestParam(required = false) String keyword) {

        List<UserAdminResponse> response = adminUserService.getUsers(keyword);
        return ResponseEntity.ok(response);
    }
}