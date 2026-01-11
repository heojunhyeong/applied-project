package com.team.wearly.domain.user.repository;

import com.team.wearly.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserNickname(String userNickname);
    Optional<User> findByUserName(String userName);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserNickname(String userNickname);
    boolean existsByUserName(String userName);
}
