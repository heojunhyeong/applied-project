package com.team.wearly.domain.user.repository;

import com.team.wearly.domain.user.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUserEmail(String userEmail);
    Optional<Seller> findByUserNickname(String userNickname);
    Optional<Seller> findByUserName(String userName);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserNickname(String userNickname);
    boolean existsByUserName(String userName);

    // 내 닉네임 제외 중복 체크(프로필 수정용)
    boolean existsByUserNicknameAndIdNot(String userNickname, Long id);
}