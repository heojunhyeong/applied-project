package com.team.wearly.domain.seller.service;

import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.domain.seller.dto.request.SellerProfileUpdateRequest;
import com.team.wearly.domain.seller.dto.response.SellerProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerProfileService {

    private final SellerRepository sellerRepository;

    /** 판매자 프로필 조회 */
    public SellerProfileResponse getProfile(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        return SellerProfileResponse.from(seller);
    }

    /** 판매자 프로필 수정 (닉네임/소개/연락처) */
    @Transactional
    public SellerProfileResponse updateProfile(Long sellerId, SellerProfileUpdateRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        String newNickname = request.userNickname() == null ? null : request.userNickname().trim();
        String newIntro = request.introduction();   // null 가능
        String newPhone = request.phoneNumber();    // null 가능

        if (newNickname == null || newNickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }

        // 닉네임 중복 체크(내 id 제외)
        if (sellerRepository.existsByUserNicknameAndIdNot(newNickname, seller.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        seller.updateProfile(newNickname, newIntro, newPhone);

        return SellerProfileResponse.from(seller);
    }

    // S3업로드 후 url을 DB에 저장
    @Transactional
    public SellerProfileResponse updateProfileImage(Long sellerId, String imageUrl) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));
        seller.updateImageUrl(imageUrl);
        return SellerProfileResponse.from(seller);
    }
}
