package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.request.UpdateSellerRequest;
import com.team.wearly.domain.user.dto.request.UpdateUserRequest;
import com.team.wearly.domain.user.dto.response.AdminSellerResponse;
import com.team.wearly.domain.user.dto.response.UserAdminResponse;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.entity.enums.UserRole;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화)
public class AdminUserService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    /**
     * 회원 목록 조회 (검색 기능 포함)
     * keyword가 있으면 검색, 없으면 전체 조회
     */
    public List<UserAdminResponse> getUsers(String keyword) {
        List<User> users;

        // 검색어가 유효한지(null이 아니고 빈 문자열이 아닌지) 체크
        if (keyword != null && !keyword.isBlank()) {
            users = userRepository.searchByKeyword(keyword);
        } else {
            users = userRepository.findAll();
        }

        // 가져온 User 엔티티 리스트를 UserAdminResponse DTO 리스트로 변환하여 반환
        return users.stream()
                .map(UserAdminResponse::from)
                .toList();
    }

    /**
     * 판매자 목록 조회 (검색 기능 포함)
     * keyword가 있으면 검색, 없으면 전체 조회
     */
    public List<AdminSellerResponse> getSellers(String keyword) {
        List<Seller> sellers;

        // 검색어가 유효한지(null이 아니고 빈 문자열이 아닌지) 체크
        if (keyword != null && !keyword.isBlank()) {
            sellers = sellerRepository.searchByKeyword(keyword);
        } else {
            sellers = sellerRepository.findAll();
        }

        // 가져온 Seller 엔티티 리스트를 AdminSellerResponse DTO 리스트로 변환하여 반환
        return sellers.stream()
                .map(AdminSellerResponse::from)
                .toList();
    }

    /**
     * 특정 회원 조회 (User 또는 Seller)
     * @param userId 회원 ID
     * @param userType 회원 타입 (USER 또는 SELLER)
     * @return UserAdminResponse 또는 AdminSellerResponse
     */
    public Object getUser(Long userId, UserRole userType) {
        if (userType == UserRole.USER) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + userId));
            return UserAdminResponse.from(user);
        } else if (userType == UserRole.SELLER) {
            Seller seller = sellerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다: " + userId));
            return AdminSellerResponse.from(seller);
        } else {
            throw new IllegalArgumentException("지원하지 않는 회원 타입입니다: " + userType);
        }
    }

    /**
     * User 소프트 삭제
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + userId));
        
        user.softDelete();
    }

    /**
     * User 정보 수정
     */
    @Transactional
    public void updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + userId));
        
        // 이메일 중복 체크 (본인 제외)
        if (userRepository.existsByUserEmail(request.getUserEmail()) && 
            !user.getUserEmail().equals(request.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getUserEmail());
        }
        
        // 닉네임 중복 체크 (본인 제외)
        if (userRepository.existsByUserNickname(request.getUserNickname()) && 
            !user.getUserNickname().equals(request.getUserNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다: " + request.getUserNickname());
        }
        
        user.updateInfo(request.getUserEmail(), request.getUserNickname());
    }

    /**
     * Seller 소프트 삭제
     */
    @Transactional
    public void deleteSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다: " + sellerId));
        
        seller.softDelete();
    }

    /**
     * Seller 정보 수정
     */
    @Transactional
    public void updateSeller(Long sellerId, UpdateSellerRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다: " + sellerId));
        
        // 이메일 중복 체크 (본인 제외)
        if (sellerRepository.existsByUserEmail(request.getUserEmail()) && 
            !seller.getUserEmail().equals(request.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getUserEmail());
        }
        
        // 닉네임 중복 체크 (본인 제외)
        if (sellerRepository.existsByUserNicknameAndIdNot(request.getUserNickname(), sellerId)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다: " + request.getUserNickname());
        }
        
        seller.updateProfile(request.getUserNickname(), request.getIntroduction(), request.getPhoneNumber());
        seller.updateEmail(request.getUserEmail());
    }
}