package com.team.wearly.domain.user.entity;

import com.team.wearly.global.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "refresh_token",
        indexes = {
                @Index(name = "idx_refresh_token_username", columnList = "username"),
                @Index(name = "idx_refresh_token_token", columnList = "token")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_token_username",
                        columnNames = {"username"}
                )
        }
)
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 아이디 (User, Seller, Admin의 userName)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // Refresh Token 값
    @Column(nullable = false, length = 500)
    private String token;

    // Refresh Token 만료 시간
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // Refresh Token 업데이트
    public void updateToken(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    // Refresh Token 만료 확인
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
