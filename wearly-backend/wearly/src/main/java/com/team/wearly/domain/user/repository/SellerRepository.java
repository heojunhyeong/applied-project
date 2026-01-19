package com.team.wearly.domain.user.repository;

import com.team.wearly.domain.user.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    // 아이디(userName) 또는 닉네임(userNickname)에 키워드가 포함된 판매자 검색
    @Query("SELECT s FROM Seller s WHERE (s.userName LIKE %:keyword% OR s.userNickname LIKE %:keyword%) AND s.deletedAt IS NULL")
    List<Seller> searchByKeyword(@Param("keyword") String keyword);
    
    // 차단되지 않은 모든 판매자 조회 (deletedAt이 null인 것만)
    @Query("SELECT s FROM Seller s WHERE s.deletedAt IS NULL")
    List<Seller> findAllActive();
}