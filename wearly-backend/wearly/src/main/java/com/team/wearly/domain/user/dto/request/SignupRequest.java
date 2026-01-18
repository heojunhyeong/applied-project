package com.team.wearly.domain.user.dto.request;

import com.team.wearly.domain.user.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatch  // 비밀번호 재확인 검증
public class SignupRequest {
    
    @NotBlank(message = "아이디는 필수입니다")
    @NoAdminWord(message = "아이디에 'admin'이라는 단어는 사용할 수 없습니다")
    private String userId;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Password  // 최소 8자, 특수문자 포함 검증
    @NoAdminWord(message = "비밀번호에 'admin'이라는 단어는 사용할 수 없습니다")
    private String userPassword;
    
    @NotBlank(message = "비밀번호 재확인은 필수입니다")
    @NoAdminWord(message = "비밀번호 재확인에 'admin'이라는 단어는 사용할 수 없습니다")
    private String confirmPassword;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 30, message = "이메일은 30자 이하여야 합니다")
    @NoAdminWord(message = "이메일에는 'admin'이라는 단어는 사용할 수 없습니다")
    private String userEmail;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 12, message = "닉네임은 12자 이하여야 합니다")
    @NoAdminWord(message = "닉네임에는 'admin'이라는 단어는 사용할 수 없습니다")
    private String nickName;
    
    @NotNull(message = "권한은 필수입니다")
    @AllowedRole  // USER 또는 SELLER만 허용 (ADMIN 불가)
    private UserRole roleType;
}
