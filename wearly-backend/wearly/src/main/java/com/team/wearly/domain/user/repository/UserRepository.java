package com.team.wearly.domain.user.repository;

import com.team.wearly.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
