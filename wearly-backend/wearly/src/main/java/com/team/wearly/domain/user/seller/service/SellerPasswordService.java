package com.team.wearly.domain.user.seller.service;

import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.entity.enums.UserRole;
import com.team.wearly.domain.user.seller.dto.request.SellerPasswordChangeRequest;
import com.team.wearly.domain.user.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerPasswordService {

    private static final Long FIXED_SELLER_ID = 1L;

    private final SellerRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(SellerPasswordChangeRequest request) {
        User seller = userRepository.findByIdAndUserRole(FIXED_SELLER_ID, UserRole.SELLER)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        // 1. 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.currentPassword(), seller.getUserPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2. 새 비밀번호 확인
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 3. 비밀번호 변경
        seller.changePassword(passwordEncoder.encode(request.newPassword()));
    }
}
