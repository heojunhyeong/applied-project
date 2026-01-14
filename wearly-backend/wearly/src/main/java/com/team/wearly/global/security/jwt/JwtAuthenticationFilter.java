package com.team.wearly.global.security.jwt;

import com.team.wearly.domain.user.entity.enums.UserRole;
import com.team.wearly.domain.user.repository.AdminRepository;
import com.team.wearly.domain.user.repository.SellerRepository;
import com.team.wearly.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.extractUsername(jwt);
                String role = jwtTokenProvider.extractClaim(jwt, claims -> claims.get("role", String.class));
                
                logger.info("JWT 인증 시도 - Username: {}, Role: {}", username, role);

                // 역할에 따라 적절한 테이블에서 사용자 정보 조회
                Object userEntity = null;
                
                if (UserRole.USER.name().equals(role)) {
                    userEntity = userRepository.findByUserName(username).orElse(null);
                } else if (UserRole.SELLER.name().equals(role)) {
                    userEntity = sellerRepository.findByUserName(username).orElse(null);
                } else if (UserRole.ADMIN.name().equals(role)) {
                    userEntity = adminRepository.findByUserName(username).orElse(null);
                    logger.info("Admin 조회 결과: {}", userEntity != null ? "찾음" : "없음");
                }

                if (userEntity != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 권한 설정
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    logger.info("권한 설정: ROLE_{}", role);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userEntity,
                                    null,
                                    Collections.singletonList(authority)
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("인증 정보 설정 완료 - 권한: ROLE_{}, 사용자: {}", role, username);
                    logger.info("설정된 권한 목록: {}", authentication.getAuthorities());
                } else {
                    if (userEntity == null) {
                        logger.warn("사용자 엔티티를 찾을 수 없음 - Username: {}, Role: {}", username, role);
                    }
                }
            } else {
                if (!StringUtils.hasText(jwt)) {
                    logger.warn("JWT 토큰이 없음 - URI: {}", request.getRequestURI());
                } else {
                    logger.warn("JWT 토큰 유효성 검증 실패 - URI: {}", request.getRequestURI());
                }
            }
        } catch (Exception e) {
            logger.error("JWT 인증 중 오류 발생", e);
        }

        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 추출
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.info("요청 URI: {}, Authorization 헤더 존재: {}", 
            request.getRequestURI(), 
            StringUtils.hasText(bearerToken));
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.info("토큰 추출 성공 (길이: {})", token.length());
            return token;
        }
        logger.warn("토큰 추출 실패 - 헤더: {}", bearerToken != null ? bearerToken.substring(0, Math.min(20, bearerToken.length())) + "..." : "null");
        return null;
    }
}
