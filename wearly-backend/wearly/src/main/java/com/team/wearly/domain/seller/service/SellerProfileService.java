package com.team.wearly.domain.seller.service;

import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.domain.seller.dto.request.SellerProfileUpdateRequest;
import com.team.wearly.domain.seller.dto.response.SellerProfileResponse;
import com.team.wearly.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerProfileService {

    private final SellerRepository sellerRepository;
    private final S3Service s3Service;

    /**
     * 판매자 식별자를 통해 현재 등록된 프로필 정보를 조회함
     *
     * @param sellerId 조회할 판매자의 식별자
     * @return 판매자 프로필 정보 응답 DTO
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
    public SellerProfileResponse getProfile(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        return SellerProfileResponse.from(seller);
    }


    /**
     * 판매자의 닉네임, 소개글, 연락처 정보를 수정하며, 닉네임의 경우 중복 검증을 수행함
     *
     * @param sellerId 수정을 요청한 판매자의 식별자
     * @param request 변경할 프로필 정보(닉네임, 소개글, 연락처)
     * @return 수정이 완료된 프로필 정보 응답 DTO
     * @throws IllegalArgumentException 닉네임이 비어있거나 이미 타 사용자가 사용 중인 경우 발생
     * @author 허보미
     * @DateOfCreated 2026-01-12
     * @DateOfEdit 2026-01-12
     */
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


    /**
     * S3 등 외부 스토리지에 업로드된 프로필 이미지의 경로를 DB에 저장함
     * 기존 프로필 이미지가 있는 경우 S3에서 삭제함
     *
     * @param sellerId 판매자 식별자
     * @param imageUrl 업로드 완료된 이미지의 공개 URL
     * @return 이미지가 업데이트된 프로필 정보 응답 DTO
     * @author 정찬혁
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @Transactional
    public SellerProfileResponse updateProfileImage(Long sellerId, String imageUrl) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        // 기존 이미지가 있는 경우 S3에서 삭제
        String oldImageUrl = seller.getImageUrl();    //기존 이미지
        if (oldImageUrl != null && !oldImageUrl.isBlank() && !oldImageUrl.equals(imageUrl)) {
            try {
                String oldKey = s3Service.extractKeyFromUrl(oldImageUrl);
                if (oldKey != null) {
                    s3Service.deleteObject(oldKey);
                }
            } catch (Exception e) {
                // 에러 발생 시 무시 (로그만 남기고 계속 진행)
            }
        }

        seller.updateImageUrl(imageUrl);
        return SellerProfileResponse.from(seller);
    }
}
