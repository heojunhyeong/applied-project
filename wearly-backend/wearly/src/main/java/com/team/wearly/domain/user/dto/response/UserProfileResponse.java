package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String userName,
        String userEmail,
        String userNickname,
        String introduction,
        String phoneNumber,
        String imageUrl,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserNickname(),
                user.getIntroduction(),
                user.getPhoneNumber(),
                user.getImageUrl(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
    }
}