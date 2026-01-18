package com.team.wearly.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 재설정 요청 DTO
 * 해당 이메일로 링크 보내줘라고 요청할때 사용
 * TODO: 주석 추가 필요
 */
@Getter
@NoArgsConstructor
public class PasswordResetRequestDto {
    private String email;
}