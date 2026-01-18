package com.team.wearly.global.config;

import com.team.wearly.domain.user.entity.Admin;
import com.team.wearly.domain.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_ADMIN_USERNAME = "admin123";
    
    /**
     * 애플리케이션 시작 시 기본 admin 계정 생성 (admin 테이블에 저장)
     * 아이디: admin123
     * 비밀번호: admin123!
     * 사용자가 직접 삭제하지 않는 이상 삭제되지 않습니다.
     */
    @Bean
    public CommandLineRunner initDefaultAdmin() {
        return args -> {
            // admin123 계정이 이미 존재하는지 확인 (admin 테이블에서)
            boolean adminExists = adminRepository.findByUserName(DEFAULT_ADMIN_USERNAME)
                    .isPresent();
            
            if (!adminExists) {
                // admin123 계정 생성 (admin 테이블에 저장)
                Admin admin = Admin.builder()
                        .userName(DEFAULT_ADMIN_USERNAME)
                        .userPassword(passwordEncoder.encode("admin123!"))  // 비밀번호: admin123!
                        .userEmail("admin@wearly.com")
                        .userNickname("관리자")
                        .build();
                
                Admin savedAdmin = adminRepository.save(admin);
                logger.info("기본 관리자 계정이 생성되었습니다. 테이블: admin, 사용자명: admin123, 이메일: admin@wearly.com, 닉네임: 관리자");
            } else {
                logger.info("기본 관리자 계정(admin123)이 이미 admin 테이블에 존재합니다.");
            }
        };
    }
}
