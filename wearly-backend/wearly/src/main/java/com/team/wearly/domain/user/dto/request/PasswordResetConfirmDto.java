package com.team.wearly.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 변경 확정 DTO
 * 링크를 타고 들어와서 바꿔달라고 요청할때 사용
 */
@Getter
@NoArgsConstructor
public class PasswordResetConfirmDto {
    private String token;
    private String newPassword;
}