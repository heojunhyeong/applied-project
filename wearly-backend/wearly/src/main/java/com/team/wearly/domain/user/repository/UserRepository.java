package com.team.wearly.domain.user.repository;

import com.team.wearly.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserNickname(String userNickname);
    Optional<User> findByUserName(String userName);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserNickname(String userNickname);
    boolean existsByUserName(String userName);

    // 아이디(userName) 또는 닉네임(userNickname)에 키워드가 포함된 유저 검색
    //findby 방식을 사용했을때 너무 난잡해보여서 query 어노테이션을 사용
    @Query("SELECT u FROM User u WHERE (u.userName LIKE %:keyword% OR u.userNickname LIKE %:keyword%) AND u.deletedAt IS NULL")
    List<User> searchByKeyword(@Param("keyword") String keyword);
    
    // 차단되지 않은 모든 사용자 조회 (deletedAt이 null인 것만)
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActive();

    // 내(user) 닉네임 제외 중복 체크(프로필 수정용)
    boolean existsByUserNicknameAndIdNot(String userNickname, Long id);
}
