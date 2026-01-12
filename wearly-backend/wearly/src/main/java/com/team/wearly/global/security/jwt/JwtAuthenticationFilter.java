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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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

                // 역할에 따라 적절한 테이블에서 사용자 정보 조회
                Object userEntity = null;
                
                if (UserRole.USER.name().equals(role)) {
                    userEntity = userRepository.findByUserName(username).orElse(null);
                } else if (UserRole.SELLER.name().equals(role)) {
                    userEntity = sellerRepository.findByUserName(username).orElse(null);
                } else if (UserRole.ADMIN.name().equals(role)) {
                    userEntity = adminRepository.findByUserName(username).orElse(null);
                }

                if (userEntity != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 권한 설정
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userEntity,
                                    null,
                                    Collections.singletonList(authority)
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
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
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
