package cn.gdsdxy.campustrading.common.util;

import cn.gdsdxy.campustrading.common.model.LoginUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    public JwtFilter() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && !jwtUtils.isTokenExpired(token)) {
            try {
                Claims claims = jwtUtils.getClaimsFromToken(token);
                if (claims != null) {
                    Long userId = ((Number) claims.get("userId")).longValue();
                    String username = claims.get("username", String.class);

                    log.info("JWT解析成功: userId={}, username={}", userId, username);

                    LoginUser loginUser = new LoginUser(userId, username, Collections.emptyList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

                    // ✅ 关键：设置 Authentication
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("SecurityContext 设置成功: {}", SecurityContextHolder.getContext().getAuthentication());
                }
            } catch (Exception e) {
                log.error("JWT Token解析失败", e);
                SecurityContextHolder.clearContext(); // 清理上下文
            }
        }

        filterChain.doFilter(request, response);
    }
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        // ✅ 去掉 Bearer 检查逻辑
        if (StringUtils.hasText(token)) {
            // 如果包含 Bearer 前缀，则去掉；否则直接返回
            if (token.startsWith("Bearer ")) {
                return token.substring(7);
            }
            return token; // 直接返回裸 Token
        }
        return null;
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
    }
}