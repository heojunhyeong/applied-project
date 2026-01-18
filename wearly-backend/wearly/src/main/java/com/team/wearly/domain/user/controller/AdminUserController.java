package com.team.wearly.domain.user.controller;

import com.team.wearly.domain.user.dto.request.UpdateSellerRequest;
import com.team.wearly.domain.user.dto.request.UpdateUserRequest;
import com.team.wearly.domain.user.dto.response.AdminSellerResponse;
import com.team.wearly.domain.user.dto.response.UserAdminResponse;
import com.team.wearly.domain.user.entity.enums.UserRole;
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
     * 관리자 권한으로 회원(User/Seller) 목록을 조회함. 역할(Role) 필터링 및 키워드 검색을 지원함
     *
     * @param keyword 검색어 (닉네임, 이름 등)
     * @param userType 회원 구분 (USER, SELLER 등)
     * @return 각 타입에 맞는 회원 정보 리스트 (UserAdminResponse 또는 AdminSellerResponse)
     * @author 최윤혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-15
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole userType) {

        // userType이 없으면 기존처럼 User만 반환 (하위 호환성)
        if (userType == null || userType == UserRole.USER) {
            List<UserAdminResponse> response = adminUserService.getUsers(keyword);
            return ResponseEntity.ok(response);
        } else if (userType == UserRole.SELLER) {
            List<AdminSellerResponse> response = adminUserService.getSellers(keyword);
            return ResponseEntity.ok(response);
        } else {
            // ADMIN은 조회하지 않음
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 특정 회원(일반 사용자 또는 판매자)의 상세 가입 정보와 계정 상태를 조회함
     *
     * @param userId 회원 식별자
     * @param userType 회원 구분 필드 (USER/SELLER)
     * @return 회원 상세 정보 객체
     * @author 최윤혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getUser(
            @PathVariable Long userId,
            @RequestParam UserRole userType) {

        Object response = adminUserService.getUser(userId, userType);
        return ResponseEntity.ok(response);
    }


    /**
     * 일반 사용자 계정을 소프트 삭제 처리하여 플랫폼 이용을 중단시킴
     *
     * @param userId 삭제할 사용자의 식별자
     * @return 성공 시 204 No Content
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자가 일반 사용자의 프로필 정보나 계정 설정을 강제로 수정함
     *
     * @param userId 수정할 사용자의 식별자
     * @param request 변경할 사용자 정보 DTO
     * @return 성공 시 200 OK
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
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
     * 판매자 계정을 소프트 삭제 처리함
     *
     * @param sellerId 삭제할 판매자의 식별자
     * @return 성공 시 204 No Content
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    @DeleteMapping("/sellers/{sellerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long sellerId) {
        adminUserService.deleteSeller(sellerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자가 판매자의 브랜드 정보나 연락처 등 사업자 정보를 강제로 수정함
     *
     * @param sellerId 수정할 판매자의 식별자
     * @param request 변경할 판매자 정보 DTO
     * @return 성공 시 200 OK
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
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