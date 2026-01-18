package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.user.entity.Admin;

import java.time.LocalDateTime;

public record AdminProfileResponse(
        Long id,
        String userName,
        String userEmail,
        String userNickname,
        String introduction,
        String phoneNumber,
        String imageUrl,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
    public static AdminProfileResponse from(Admin admin) {
        return new AdminProfileResponse(
                admin.getId(),
                admin.getUserName(),       // // Admin 로그인 아이디/이름 필드 getter에 맞게 수정
                admin.getUserEmail(),      // // Admin 이메일 getter에 맞게 수정
                admin.getUserNickname(),   // // Admin 닉네임 getter에 맞게 수정
                admin.getIntroduction(),    // // 소개 getter에 맞게 수정
                admin.getPhoneNumber(),     // // 연락처 getter에 맞게 수정
                admin.getImageUrl(),        // // 이미지 URL getter에 맞게 수정
                admin.getCreatedDate(),     // // BaseTimeEntity 필드명에 맞게 수정
                admin.getUpdatedDate()      // // BaseTimeEntity 필드명에 맞게 수정
        );
    }
}
