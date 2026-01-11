package com.team.wearly.domain.user.dto.request;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoAdminWord.NoAdminWordValidator.class)
public @interface NoAdminWord {
    String message() default "admin이라는 단어는 사용할 수 없습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    class NoAdminWordValidator implements ConstraintValidator<NoAdminWord, String> {
        @Override
        public void initialize(NoAdminWord constraintAnnotation) {
        }
        
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isEmpty()) {
                return true; // null이나 빈 값은 다른 validator가 처리
            }
            
            // 대소문자 구분 없이 "admin" 단어 포함 여부 체크
            String lowerValue = value.toLowerCase();
            return !lowerValue.contains("admin");
        }
    }
}
