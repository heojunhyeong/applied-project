package com.team.wearly.domain.user.entity;

import com.team.wearly.domain.user.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, length = 30)
    private String userName;

    @Column(nullable = false, length = 255)
    private String userPassword;

    @Column(nullable = false, unique = true, length = 30)
    private String userEmail;

    @Column(nullable = false, unique = true, length = 12)
    private String userNickname;

    //연락처
    @Column(length = 255)
    private String introduction;

    //소개
    @Column(length = 20)
    private String phoneNumber;

    private String imageUrl;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    public void updateProfile(String userNickname, String introduction, String phoneNumber) {
        this.userNickname = userNickname;
        this.introduction = introduction;
        this.phoneNumber = phoneNumber;
    }

    // 소프트 삭제를 위한 필드
    @Column(nullable = true)
    private LocalDateTime deletedAt;

//    private Long membership_id;

    // 소프트 삭제 메서드
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 회원 정보 수정 메서드
    public void updateInfo(String userEmail, String userNickname) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
    }

    // 프로필 이미지 수정(변경) 메서드
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    // 패스워드 재설정 메서드
    public void changePassword(String encryptedPassword) {
        this.userPassword = encryptedPassword;
    }
}
