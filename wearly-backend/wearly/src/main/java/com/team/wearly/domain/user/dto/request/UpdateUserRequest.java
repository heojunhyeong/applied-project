package com.team.wearly.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 30, message = "이메일은 30자 이하여야 합니다")
    @NoAdminWord(message = "이메일에는 'admin'이라는 단어는 사용할 수 없습니다")
    private String userEmail;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 12, message = "닉네임은 12자 이하여야 합니다")
    @NoAdminWord(message = "닉네임에는 'admin'이라는 단어는 사용할 수 없습니다")
    private String userNickname;
}
