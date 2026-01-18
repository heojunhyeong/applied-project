package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.request.SignupRequest;
import com.team.wearly.domain.user.dto.response.MyPageResponse;
import com.team.wearly.domain.user.dto.response.SignupResponse;

public interface UserService {
    /**
     * 회원가입 메인 로직
     * - USER: user 테이블에 저장
     * - SELLER: seller 테이블에 저장
     * - ADMIN 단어 차단: 이메일, 닉네임, 비밀번호 등 모든 필드에서 검증
     */
    SignupResponse signup(SignupRequest request);


    public void requestPasswordReset(String email);

    public void resetPassword(String token, String newPassword);

    MyPageResponse getMyPageData(Long userId);

}