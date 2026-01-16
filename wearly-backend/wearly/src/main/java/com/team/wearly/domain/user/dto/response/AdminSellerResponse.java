package com.team.wearly.domain.user.dto.response;

import com.team.wearly.domain.user.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSellerResponse {
    private Long id;            // 판매자 고유 번호
    private String userName;    // 아이디
    private String userEmail;   // 이메일
    private String userNickname;// 닉네임
    private String introduction;// 소개
    private String phoneNumber; // 전화번호
    private String imageUrl;    // 프로필 이미지 URL
    private LocalDateTime createdDate; // 계정 생성 일자
    private LocalDateTime updatedDate; // 수정일

    // 엔티티 -> DTO 변환 편의 메서드
    public static AdminSellerResponse from(Seller seller) {
        return AdminSellerResponse.builder()
                .id(seller.getId())
                .userName(seller.getUserName())
                .userEmail(seller.getUserEmail())
                .userNickname(seller.getUserNickname())
                .introduction(seller.getIntroduction())
                .phoneNumber(seller.getPhoneNumber())
                .imageUrl(seller.getImageUrl())
                .createdDate(seller.getCreatedDate())
                .updatedDate(seller.getUpdatedDate())
                .build();
    }
}
