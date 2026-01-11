package com.team.wearly.domain.user.seller.service;

import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.domain.user.seller.dto.request.SellerPasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerPasswordService {

    private static final Long FIXED_SELLER_ID = 1L;

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(SellerPasswordChangeRequest request) {
        Seller seller = sellerRepository.findById(FIXED_SELLER_ID)
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
