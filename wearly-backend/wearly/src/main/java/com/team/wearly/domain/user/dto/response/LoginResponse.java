package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.user.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String userEmail;
    private String nickName;
    private UserRole role;
    private String message;
}
