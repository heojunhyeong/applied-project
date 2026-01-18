package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.request.AdminProfileUpdateRequest;
import com.team.wearly.domain.user.dto.response.AdminProfileResponse;
import com.team.wearly.domain.user.entity.Admin;
import com.team.wearly.domain.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProfileService {

    private final AdminRepository adminRepository;

    // 관리자 프로필 조회 메소드
    public AdminProfileResponse getProfile(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
        return AdminProfileResponse.from(admin);
    }

    // 관리자 프로필 수정 메소드 (닉네임/소개/연락처)
    @Transactional
    public AdminProfileResponse updateProfile(Long adminId, AdminProfileUpdateRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        String newNickname = request.adminNickname() == null ? null : request.adminNickname().trim();
        String newIntro = request.introduction();   // null 가능
        String newPhone = request.phoneNumber();    // null 가능

        if (newNickname == null || newNickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }

        // 닉네임 중복 체크(내 id 제외)
        if (adminRepository.existsByUserNicknameAndIdNot(newNickname, admin.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 닉네임 변경 메소드
        admin.updateNickname(newNickname);
        // 소개 변경 메소드
        admin.updateIntroduction(newIntro);
        // 연락처 변경 메소드
        admin.updatePhoneNumber(newPhone);

        return AdminProfileResponse.from(admin);
    }

    // 관리자 프로필 이미지 수정 메소드
    @Transactional
    public AdminProfileResponse updateProfileImage(Long adminId, String imageUrl) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        // 프로필 이미지 URL 변경 메소드
        admin.updateImageUrl(imageUrl);

        return AdminProfileResponse.from(admin);
    }
}
