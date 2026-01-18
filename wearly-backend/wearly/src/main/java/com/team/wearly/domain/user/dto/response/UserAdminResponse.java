package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminResponse {

    private Long id;            // 회원 고유 번호 (삭제/수정 시 필요하므로 포함)
    private String userName;    // 아이디
    private String userEmail;   // 이메일
    private String userNickname;// 닉네임
    private String introduction;// 소개
    private String phoneNumber; // 전화번호
    private String imageUrl;    // 프로필 이미지 URL
    private LocalDateTime createdDate; // 계정 생성 일자
    private LocalDateTime updatedDate; // 수정일

    // 엔티티 -> DTO 변환 편의 메서드
    public static UserAdminResponse from(User user) {
        return UserAdminResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .introduction(user.getIntroduction())
                .phoneNumber(user.getPhoneNumber())
                .imageUrl(user.getImageUrl())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .build();
    }
}