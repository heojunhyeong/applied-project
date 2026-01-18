package com.team.wearly.domain.seller.service;

import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.domain.seller.dto.request.SellerPasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerPasswordService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * 기존 비밀번호 일치 여부를 확인하고, 신규 비밀번호와 확인용 비밀번호가 일치할 경우 암호화하여 변경함
     *
     * @param sellerId 비밀번호를 변경할 판매자 식별자
     * @param request 현재 비밀번호, 새 비밀번호, 새 비밀번호 확인 값이 포함된 DTO
     * @throws IllegalArgumentException 판매자가 존재하지 않거나, 현재 비밀번호 불일치, 또는 새 비밀번호 확인이 불일치할 경우 발생
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public void changePassword(Long sellerId, SellerPasswordChangeRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
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
