package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.UpdateSellerRequest;
import com.team.wearly.domain.user.dto.request.UpdateUserRequest;
import com.team.wearly.domain.user.dto.response.UserAdminResponse;
import com.team.wearly.domain.user.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserAdminResponse>> getUsers(
            @RequestParam(required = false) String keyword) {

        List<UserAdminResponse> response = adminUserService.getUsers(keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자용 User 소프트 삭제 API
     * DELETE /api/admin/users/{userId}
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자용 User 정보 수정 API
     * PUT /api/admin/users/{userId}
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        adminUserService.updateUser(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 관리자용 Seller 소프트 삭제 API
     * DELETE /api/admin/users/sellers/{sellerId}
     */
    @DeleteMapping("/sellers/{sellerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long sellerId) {
        adminUserService.deleteSeller(sellerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자용 Seller 정보 수정 API
     * PUT /api/admin/users/sellers/{sellerId}
     */
    @PutMapping("/sellers/{sellerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateSeller(
            @PathVariable Long sellerId,
            @Valid @RequestBody UpdateSellerRequest request) {
        adminUserService.updateSeller(sellerId, request);
        return ResponseEntity.ok().build();
    }
}