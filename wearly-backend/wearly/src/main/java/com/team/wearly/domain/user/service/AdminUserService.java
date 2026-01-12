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
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화)
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     * 회원 목록 조회 (검색 기능 포함)
     * keyword가 있으면 검색, 없으면 전체 조회
     */
    public List<UserAdminResponse> getUsers(String keyword) {
        List<User> users;

        // 검색어가 유효한지(null이 아니고 빈 문자열이 아닌지) 체크
        if (keyword != null && !keyword.isBlank()) {
            users = userRepository.searchByKeyword(keyword);
        } else {
            users = userRepository.findAll();
        }

        // 가져온 User 엔티티 리스트를 UserAdminResponse DTO 리스트로 변환하여 반환
        return users.stream()
                .map(UserAdminResponse::from)
                .toList();
    }
}