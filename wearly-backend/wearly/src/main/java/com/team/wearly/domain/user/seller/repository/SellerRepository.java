package com.team.wearly.domain.user.seller.repository;

import com.team.wearly.domain.user.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    // "내 닉네임 제외" 중복 체크
    boolean existsByUserNicknameAndIdNot(String userNickname, Long id);

    // 추후 사용자 조회 필요
    Optional<Seller> findByUserEmail(String userEmail);
}
