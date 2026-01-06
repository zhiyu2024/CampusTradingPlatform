package cn.gdsdxy.campustrading.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // 密钥，生产环境请放在配置文件中，这里为了演示直接写死
    // 注意：HS256 算法要求密钥长度至少 32 字节
    private static final String SECRET = "CampusTradingSecureKeyForJwtTokenGeneration2026";
    private static final long EXPIRATION = 86400000L; // 24小时

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成 Token
     * @param username 用户名
     * @param role 角色 (user/admin)
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token 获取 Claims
     */
    public Claims getClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token 是否过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}
