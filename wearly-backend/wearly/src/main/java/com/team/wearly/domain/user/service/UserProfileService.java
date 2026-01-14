package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.request.UserProfileUpdateRequest;
import com.team.wearly.domain.user.dto.response.UserProfileResponse;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {
    private final UserRepository userRepository;

    /** 유저 프로필 조회 */
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return UserProfileResponse.from(user);
    }

    /** 유저 프로필 수정 (닉네임/소개/연락처) */
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String newNickname = request.userNickname() == null ? null : request.userNickname().trim();
        String newIntro = request.introduction();   // null 가능
        String newPhone = request.phoneNumber();    // null 가능

        if (newNickname == null || newNickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }

        // 닉네임 중복 체크(내 id 제외)
        if (userRepository.existsByUserNicknameAndIdNot(newNickname, user.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        user.updateProfile(newNickname, newIntro, newPhone);

        return UserProfileResponse.from(user);
    }

    // S3업로드 후 url을 DB에 저장
    @Transactional
    public UserProfileResponse updateProfileImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        user.updateImageUrl(imageUrl);
        return UserProfileResponse.from(user);
    }

}
