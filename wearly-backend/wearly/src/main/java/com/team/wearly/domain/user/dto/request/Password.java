package com.team.wearly.domain.user.dto.request;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Password.PasswordValidator.class)
public @interface Password {
    String message() default "비밀번호는 최소 8자 이상이며, 특수문자를 포함해야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    class PasswordValidator implements ConstraintValidator<Password, String> {
        // 최소 8자, 특수문자 포함 정규식
        private static final Pattern PASSWORD_PATTERN = 
            Pattern.compile("^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");
        
        @Override
        public void initialize(Password constraintAnnotation) {
        }
        
        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            if (password == null || password.isEmpty()) {
                return false;
            }
            return PASSWORD_PATTERN.matcher(password).matches();
        }
    }
}
