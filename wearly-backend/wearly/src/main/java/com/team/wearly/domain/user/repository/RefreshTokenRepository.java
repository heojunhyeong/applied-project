package com.team.wearly.domain.user.repository;

import com.team.wearly.domain.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 사용자명으로 Refresh Token 조회
    Optional<RefreshToken> findByUsername(String username);

    // 토큰 값으로 Refresh Token 조회
    Optional<RefreshToken> findByToken(String token);

    // 사용자명으로 Refresh Token 삭제
    void deleteByUsername(String username);
}
