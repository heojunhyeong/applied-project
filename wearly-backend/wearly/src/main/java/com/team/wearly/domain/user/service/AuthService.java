package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.request.LoginRequest;
import com.team.wearly.domain.user.dto.request.LogoutRequest;
import com.team.wearly.domain.user.dto.request.RefreshTokenRequest;
import com.team.wearly.domain.user.dto.response.LoginResponse;
import com.team.wearly.domain.user.entity.Admin;
import com.team.wearly.domain.user.entity.RefreshToken;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.entity.enums.UserRole;
import com.team.wearly.domain.user.repository.AdminRepository;
import com.team.wearly.domain.user.repository.RefreshTokenRepository;
import com.team.wearly.domain.user.repository.UserRepository;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AdminRepository adminRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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
            String accessToken = jwtTokenProvider.generateAccessToken(user.getUserName(), UserRole.USER.name());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserName());
            saveRefreshToken(user.getUserName(), refreshToken);
            
            return new LoginResponse(
                    accessToken,
                    refreshToken,
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
            String accessToken = jwtTokenProvider.generateAccessToken(seller.getUserName(), UserRole.SELLER.name());
            String refreshToken = jwtTokenProvider.generateRefreshToken(seller.getUserName());
            saveRefreshToken(seller.getUserName(), refreshToken);
            
            return new LoginResponse(
                    accessToken,
                    refreshToken,
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
            String accessToken = jwtTokenProvider.generateAccessToken(admin.getUserName(), UserRole.ADMIN.name());
            String refreshToken = jwtTokenProvider.generateRefreshToken(admin.getUserName());
            saveRefreshToken(admin.getUserName(), refreshToken);
            
            return new LoginResponse(
                    accessToken,
                    refreshToken,
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

    /**
     * Refresh Token을 데이터베이스에 저장함
     * 기존에 해당 사용자의 Refresh Token이 있으면 업데이트, 없으면 새로 생성
     *
     * @param username 사용자 아이디
     * @param refreshToken Refresh Token 값
     */
    @Transactional
    public void saveRefreshToken(String username, String refreshToken) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7); // 7일 후 만료

        RefreshToken tokenEntity = refreshTokenRepository.findByUsername(username)
                .orElse(RefreshToken.builder()
                        .username(username)
                        .token(refreshToken)
                        .expiryDate(expiryDate)
                        .build());

        tokenEntity.updateToken(refreshToken, expiryDate);
        refreshTokenRepository.save(tokenEntity);
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token을 발급함
     *
     * @param request Refresh Token을 포함한 요청 DTO
     * @return 새로운 Access Token과 Refresh Token이 포함된 응답 DTO
     * @throws IllegalArgumentException Refresh Token이 유효하지 않을 경우 발생
     */
    @Transactional
    public LoginResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.refreshToken();

        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new IllegalArgumentException("Refresh Token이 필요합니다.");
        }

        // 데이터베이스에서 Refresh Token 조회
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다."));

        // Refresh Token 만료 확인
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
        }

        // Refresh Token에서 사용자명 추출
        String username;
        try {
            username = jwtTokenProvider.extractUsername(refreshTokenValue);
        } catch (Exception e) {
            // 만료된 토큰에서도 사용자명 추출 시도
            username = jwtTokenProvider.extractUsernameFromExpiredToken(refreshTokenValue);
        }

        // 사용자 정보 조회하여 Role 확인
        UserRole role = null;
        Long userId = null;
        String userEmail = null;
        String userNickname = null;

        // 1) user 테이블에서 조회
        User user = userRepository.findByUserName(username).orElse(null);
        if (user != null && user.getDeletedAt() == null) {
            role = UserRole.USER;
            userId = user.getId();
            userEmail = user.getUserEmail();
            userNickname = user.getUserNickname();
        } else {
            // 2) seller 테이블에서 조회
            Seller seller = sellerRepository.findByUserName(username).orElse(null);
            if (seller != null && seller.getDeletedAt() == null) {
                role = UserRole.SELLER;
                userId = seller.getId();
                userEmail = seller.getUserEmail();
                userNickname = seller.getUserNickname();
            } else {
                // 3) admin 테이블에서 조회
                Admin admin = adminRepository.findByUserName(username).orElse(null);
                if (admin != null) {
                    role = UserRole.ADMIN;
                    userId = admin.getId();
                    userEmail = admin.getUserEmail();
                    userNickname = admin.getUserNickname();
                }
            }
        }

        if (role == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(username, role.name());

        // 새로운 Refresh Token 발급 (Rotation)
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        saveRefreshToken(username, newRefreshToken);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                userId,
                userEmail,
                userNickname,
                role,
                "토큰이 갱신되었습니다"
        );
    }

    /**
     * 로그아웃 처리
     * Refresh Token을 데이터베이스에서 삭제하여 로그아웃 처리함
     *
     * @param request 로그아웃 요청 DTO (Refresh Token 포함)
     * @throws IllegalArgumentException Refresh Token이 유효하지 않을 경우 발생
     */
    @Transactional
    public void logout(LogoutRequest request) {
        String refreshToken = request.refreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh Token이 필요합니다.");
        }

        // 데이터베이스에서 Refresh Token 조회 및 삭제
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다."));

        refreshTokenRepository.delete(tokenEntity);
    }
}
