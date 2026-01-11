package com.team.wearly.domain.user.repository;

import com.team.wearly.domain.user.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserEmail(String userEmail);
    Optional<Admin> findByUserNickname(String userNickname);
    Optional<Admin> findByUserName(String userName);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserNickname(String userNickname);
    boolean existsByUserName(String userName);
}
