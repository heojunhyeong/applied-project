package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.request.LoginRequest;
import com.team.wearly.domain.user.dto.response.LoginResponse;
import com.team.wearly.domain.user.entity.Admin;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.entity.enums.UserRole;
import com.team.wearly.domain.user.repository.AdminRepository;
import com.team.wearly.domain.user.repository.UserRepository;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 입력된 아이디와 비밀번호를 바탕으로 User, Seller, Admin 테이블을 순차적으로 확인하여 인증을 수행함
     * 인증 성공 시 해당 역할에 맞는 권한 정보를 포함한 JWT 토큰을 생성하여 반환함
     *
     * @param request 로그인 아이디(userId)와 비밀번호(userPassword)를 포함한 요청 DTO
     * @return 토큰 정보, 회원 식별자, 역할 등이 포함된 로그인 응답 DTO
     * @throws IllegalArgumentException 요청 값이 비어있거나 인증 정보가 일치하지 않을 경우 발생
     * @author 허준형
     * @DateOfCreated 2026-01-16
     * @DateOfEdit 2026-01-16
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 값이 없습니다.");
        }

        String userId = request.getUserId();
        String userPassword = request.getUserPassword();

        if (userId == null || userId.isBlank() || userPassword == null || userPassword.isBlank()) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 비어있습니다.");
        }

        // 1) user 테이블에서 조회
        User user = userRepository.findByUserName(userId).orElse(null);
       if (user != null && user.getDeletedAt() == null && passwordEncoder.matches(userPassword, user.getUserPassword())) {
            String token = jwtTokenProvider.generateToken(user.getUserName(), UserRole.USER.name());
            return new LoginResponse(
                    token,
                    "Bearer",
                    user.getId(),
                    user.getUserEmail(),
                    user.getUserNickname(),
                    UserRole.USER,
                    "로그인에 성공했습니다"
            );
        }

        // 2) seller 테이블에서 조회
        Seller seller = sellerRepository.findByUserName(userId).orElse(null);
        if (seller != null && seller.getDeletedAt() == null && passwordEncoder.matches(userPassword, seller.getUserPassword())) {
            String token = jwtTokenProvider.generateToken(seller.getUserName(), UserRole.SELLER.name());
            return new LoginResponse(
                    token,
                    "Bearer",
                    seller.getId(),
                    seller.getUserEmail(),
                    seller.getUserNickname(),
                    UserRole.SELLER,
                    "로그인에 성공했습니다"
            );
        }

        // 3) admin 테이블에서 조회
        Admin admin = adminRepository.findByUserName(userId).orElse(null);
        if (admin != null && passwordEncoder.matches(userPassword, admin.getUserPassword())) {
            String token = jwtTokenProvider.generateToken(admin.getUserName(), UserRole.ADMIN.name());
            return new LoginResponse(
                    token,
                    "Bearer",
                    admin.getId(),
                    admin.getUserEmail(),
                    admin.getUserNickname(),
                    UserRole.ADMIN,
                    "로그인에 성공했습니다"
            );
        }

        throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다");
    }
}
