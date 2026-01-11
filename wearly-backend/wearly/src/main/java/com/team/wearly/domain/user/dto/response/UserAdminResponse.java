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
    private LocalDateTime createdDate; // 계정 생성 일자

    // 엔티티 -> DTO 변환 편의 메서드
    public static UserAdminResponse from(User user) {
        return UserAdminResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .createdDate(user.getCreatedDate())
                .build();
    }
}