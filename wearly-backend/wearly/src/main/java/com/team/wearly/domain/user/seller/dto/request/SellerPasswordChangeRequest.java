package com.team.wearly.domain.user.seller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerPasswordChangeRequest(

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String currentPassword,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "새 비밀번호는 8~20자여야 합니다.")
        String newPassword,

        @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
        String newPasswordConfirm
) {
}