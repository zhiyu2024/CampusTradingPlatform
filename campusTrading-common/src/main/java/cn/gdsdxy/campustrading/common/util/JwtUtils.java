package cn.gdsdxy.campustrading.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;  // 新增
import org.springframework.beans.factory.annotation.Value;  // 新增
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;  // 新增
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // 从配置文件读取密钥（新增）
    @Value("${jwt.secret}")
    private String secretKey;

    // 过期时间：7天
    private static final long EXPIRATION = 604800000L;

    /**
     * 生成 Token
     */
    public String createToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        // 将字符串密钥转为 SecretKey（关键修复）
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS512)  // 注意参数顺序变化
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims getClaimsFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());  // 新增
            return Jwts.parserBuilder()  // 改用 parserBuilder()
                    .setSigningKey(key)   // 使用 SecretKey
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims == null || claims.getExpiration().before(new Date());
    }
}