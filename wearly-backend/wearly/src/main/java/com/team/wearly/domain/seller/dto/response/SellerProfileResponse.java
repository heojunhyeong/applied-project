package com.team.wearly.domain.seller.dto.response;

import com.team.wearly.domain.user.entity.Seller;

import java.time.LocalDateTime;

public record SellerProfileResponse(
        Long id,
        String userName,
        String userEmail,
        String userNickname,
        String introduction,
        String phoneNumber,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
    public static SellerProfileResponse from(Seller seller) {
        return new SellerProfileResponse(
                seller.getId(),
                seller.getUserName(),
                seller.getUserEmail(),
                seller.getUserNickname(),
                seller.getIntroduction(),
                seller.getPhoneNumber(),
                seller.getCreatedDate(),
                seller.getUpdatedDate()
        );
    }
}