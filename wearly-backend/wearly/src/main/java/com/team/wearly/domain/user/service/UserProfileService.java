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

    /**
     * 특정 사용자의 식별자를 통해 현재 프로필 정보를 조회함
     *
     * @param userId 조회할 사용자의 PK
     * @return 닉네임, 이미지 URL 등을 포함한 프로필 응답 DTO
     * @throws IllegalArgumentException 사용자를 찾을 수 없을 경우 발생
     * @author 정찬혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return UserProfileResponse.from(user);
    }

    /**
     * 사용자의 닉네임, 소개글, 연락처 정보를 수정함
     * 닉네임의 경우 필수값이며, 본인을 제외한 타 사용자와 중복되지 않아야 함
     *
     * @param userId  수정할 사용자의 PK
     * @param request 수정하고자 하는 프로필 데이터 세트
     * @return 수정이 완료된 프로필 응답 DTO
     * @throws IllegalArgumentException 닉네임이 누락되었거나 이미 존재하는 닉네임일 경우 발생
     * @author 정찬혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
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

    /**
     * S3 등 외부 저장소에 업로드된 프로필 이미지의 URL을 사용자의 계정 정보에 반영함
     *
     * @param userId   이미지를 업데이트할 사용자의 PK
     * @param imageUrl 업로드 완료된 이미지의 전체 URL 경로
     * @return 업데이트된 프로필 정보
     * @author 정찬혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    @Transactional
    public UserProfileResponse updateProfileImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        user.updateImageUrl(imageUrl);
        return UserProfileResponse.from(user);
    }

}
