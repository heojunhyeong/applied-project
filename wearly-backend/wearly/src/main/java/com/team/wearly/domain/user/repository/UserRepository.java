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
    @Query("SELECT u FROM User u WHERE u.userName LIKE %:keyword% OR u.userNickname LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);
}

