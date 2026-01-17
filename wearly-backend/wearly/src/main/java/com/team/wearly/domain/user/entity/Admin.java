package com.team.wearly.domain.user.entity;

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
@Table(name = "admin")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
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

    // 관리자 닉네임 변경 메소드
    public void updateNickname(String adminNickname) {
        this.userNickname = adminNickname;
    }

    // 관리자 소개 변경 메소드
    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    // 관리자 연락처 변경 메소드
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // 관리자 프로필 이미지 URL 변경 메소드
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateProfile(String userNickname, String introduction, String phoneNumber) {
        this.userNickname = userNickname;
        this.introduction = introduction;
        this.phoneNumber = phoneNumber;
    }

}
