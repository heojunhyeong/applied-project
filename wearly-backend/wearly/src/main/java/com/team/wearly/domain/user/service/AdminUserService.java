package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.response.UserAdminResponse;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;

    public List<UserAdminResponse> getUsers(String keyword) {
        List<User> users;

        // 키워드가 있으면 검색, 없으면 전체 조회
        if (keyword != null && !keyword.isBlank()) {
            users = userRepository.searchByKeyword(keyword);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(UserAdminResponse::from)
                .toList();
    }
}