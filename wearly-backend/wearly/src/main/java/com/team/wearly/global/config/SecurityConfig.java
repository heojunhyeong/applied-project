package com.team.wearly.global.config;

import com.team.wearly.domain.product.entity.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 테스트를 위해 CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 이 줄을 추가하여 결제 관련 API는 누구나 접근 가능하게 만듭니다.
                        .requestMatchers("/api/payment/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}