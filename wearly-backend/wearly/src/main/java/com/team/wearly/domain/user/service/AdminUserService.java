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
     * 전체 일반 사용자 목록을 조회하거나 키워드를 통해 특정 사용자를 검색함
     *
     * @param keyword 검색어 (닉네임, 이메일 등)
     * @return 관리자용 사용자 정보 응답 DTO 리스트
     * @author 김지번
     * @DateOfCreated 2026-01-11
     * @DateOfEdit 2026-01-11
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
     * 전체 판매자 목록을 조회하거나 키워드를 통해 특정 판매자를 검색함
     *
     * @param keyword 검색어 (브랜드명, 닉네임 등)
     * @return 관리자용 판매자 정보 응답 DTO 리스트
     * @author 최윤혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
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
     * 특정 식별자와 타입을 가진 회원(User 또는 Seller)의 상세 정보를 조회함
     *
     * @param userId   회원 식별자
     * @param userType 회원 역할 구분 (USER/SELLER)
     * @return 회원 타입에 따른 DTO 객체 (UserAdminResponse 또는 AdminSellerResponse)
     * @throws IllegalArgumentException 지원하지 않는 타입이거나 회원을 찾을 수 없을 때 발생
     * @author 최윤혁
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
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
     * 일반 사용자 계정을 비활성화(소프트 삭제) 처리함
     *
     * @param userId 삭제할 사용자의 식별자
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + userId));
        
        user.softDelete();
    }


    /**
     * 일반 사용자의 핵심 정보(이메일, 닉네임)를 관리자 권한으로 수정하며, 중복 검증을 수행함
     *
     * @param userId  수정할 사용자의 식별자
     * @param request 변경할 정보가 담긴 DTO
     * @throws IllegalArgumentException 중복된 이메일이나 닉네임이 존재할 경우 발생
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
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
     * 판매자 계정을 비활성화(소프트 삭제) 처리함
     *
     * @param sellerId 삭제할 판매자의 식별자
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    @Transactional
    public void deleteSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다: " + sellerId));
        
        seller.softDelete();
    }


    /**
     * 판매자의 프로필 및 계정 정보를 관리자 권한으로 수정함
     *
     * @param sellerId 수정할 판매자의 식별자
     * @param request  변경할 판매자 상세 정보 DTO
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
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