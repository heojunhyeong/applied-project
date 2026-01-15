package com.team.wearly.global.config;

import com.team.wearly.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000", "http://localhost:70"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/signup").permitAll()
                .requestMatchers("/api/auth/login").permitAll()  // 로그인 API 허용
                            .requestMatchers("/api/users/profile/presigned-url").permitAll()    // user profile 이미지 업로드 테스트용
                            .requestMatchers("/api/seller/profile/presigned-url").permitAll()    // seller profile 이미지 업로드 테스트용
                            .requestMatchers("/api/users/profile").permitAll()  // user profile 이미지 업로드 테스트용
                            .requestMatchers("/api/seller/profile").permitAll()  // eller profile 이미지 업로드 테스트용
                            .requestMatchers("/api/payment/**", "/api/orders/**", "/api/users/orders/**").permitAll()
                .requestMatchers("/api/users/reviews").permitAll()
                .requestMatchers("/api/password/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")  // ADMIN 전용
                .requestMatchers("/api/seller/**").hasRole("SELLER") //SELLER파트 전용
                .requestMatchers("/api/products/seller/**").hasRole("SELLER")
                .anyRequest().authenticated()  // 나머지는 JWT 토큰 필요
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
