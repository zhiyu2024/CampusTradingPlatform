
package cn.gdsdxy.campustrading.common.util;

import cn.gdsdxy.campustrading.common.model.LoginUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        // ✅ 关键修复：所有/public/接口直接放行，不进行Token验证
        if (request.getRequestURI().contains("/public/")) {
            log.debug("公开接口，直接放行: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 关键修复：所有/admin/接口直接放行，暂时不进行Token验证
        if (request.getRequestURI().contains("/api/admin/")) {
            log.debug("管理员接口，直接放行: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 没有Token，放行（后面会被 Spring Security 拦截）
        if (!StringUtils.hasText(token)) {
            log.debug("请求无Token，放行: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Token过期，放行
        if (jwtUtils.isTokenExpired(token)) {
            log.debug("Token已过期，放行: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            if (claims != null) {
                Long userId = ((Number) claims.get("userId")).longValue();
                String username = claims.get("username", String.class);

                log.info("JWT认证成功: userId={}, username={}", userId, username);

                LoginUser loginUser = new LoginUser(userId, username, Collections.emptyList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("JWT Token解析失败", e);
            // ✅ 解析失败也放行，让 Spring Security 处理
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}