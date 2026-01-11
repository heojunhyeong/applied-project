package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.user.entity.User;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminResponse {
    private Long id;            // 내부 식별용 ID
    private String userName;    // 아이디
    private String userEmail;   // 이메일
    private String userNickname;// 닉네임
    private LocalDateTime createdDate; // 가입일

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