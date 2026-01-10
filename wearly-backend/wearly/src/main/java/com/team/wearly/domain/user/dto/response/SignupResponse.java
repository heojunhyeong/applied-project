package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.user.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {
    private Long id;
    private String userId;
    private String userEmail;
    private String nickName;
    private UserRole roleType;
    private String message;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
