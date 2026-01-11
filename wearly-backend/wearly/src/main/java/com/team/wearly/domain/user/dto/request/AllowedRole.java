package com.team.wearly.domain.user.dto.request;

import com.team.wearly.domain.user.entity.enums.UserRole;
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
@Constraint(validatedBy = AllowedRole.AllowedRoleValidator.class)
public @interface AllowedRole {
    String message() default "USER 또는 SELLER 역할만 선택할 수 있습니다. ADMIN으로는 회원가입할 수 없습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    class AllowedRoleValidator implements ConstraintValidator<AllowedRole, UserRole> {
        @Override
        public void initialize(AllowedRole constraintAnnotation) {
        }
        
        @Override
        public boolean isValid(UserRole roleType, ConstraintValidatorContext context) {
            if (roleType == null) {
                return false; // null은 허용하지 않음
            }
            
            // USER와 SELLER만 허용, ADMIN은 거부
            return roleType == UserRole.USER || roleType == UserRole.SELLER;
        }
    }
}
