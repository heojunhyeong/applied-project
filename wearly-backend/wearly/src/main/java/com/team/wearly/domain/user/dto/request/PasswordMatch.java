package com.team.wearly.domain.user.dto.request;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatch.PasswordMatchValidator.class)
public @interface PasswordMatch {
    String message() default "비밀번호와 비밀번호 재확인이 일치하지 않습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, SignupRequest> {
        @Override
        public void initialize(PasswordMatch constraintAnnotation) {
        }
        
        @Override
        public boolean isValid(SignupRequest request, ConstraintValidatorContext context) {
            if (request == null) {
                return true;
            }
            String password = request.getUserPassword();
            String confirmPassword = request.getConfirmPassword();
            
            if (password == null || confirmPassword == null) {
                return true; // null 체크는 @NotBlank가 처리
            }
            
            return password.equals(confirmPassword);
        }
    }
}
