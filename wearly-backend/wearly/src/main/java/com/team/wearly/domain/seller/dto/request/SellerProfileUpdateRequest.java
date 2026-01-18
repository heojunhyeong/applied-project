package com.team.wearly.domain.seller.dto.request;

import com.team.wearly.domain.user.dto.request.NoAdminWord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SellerProfileUpdateRequest(

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 12, message = "닉네임은 12자 이하여야 합니다")
        @NoAdminWord(message = "닉네임에는 'admin'이라는 단어는 사용할 수 없습니다")
        String userNickname,

        @Size(max = 255, message = "소개는 255자 이내여야 합니다.")
        String introduction,

        @Size(max = 20, message = "연락처는 20자 이내여야 합니다.")
        @Pattern(
                regexp = "^(|01[016789][-]?\\d{3,4}[-]?\\d{4}|02[-]?\\d{3,4}[-]?\\d{4}|0[3-6][1-5][-]?\\d{3,4}[-]?\\d{4})$",
                message = "전화번호 형식이 올바르지 않습니다."
        )
        String phoneNumber,

        String imageUrl
) {
}
