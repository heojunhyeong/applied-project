package com.team.wearly.domain.user.seller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerProfileUpdateRequest(

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 12, message = "닉네임은 12자 이내여야 합니다.")
        String userNickname,

        @Size(max = 255, message = "소개는 255자 이내여야 합니다.")
        String introduction,

        @Size(max = 20, message = "연락처는 20자 이내여야 합니다.")
        String phoneNumber

        // 추후 이미지 추가 예정
) {
}
