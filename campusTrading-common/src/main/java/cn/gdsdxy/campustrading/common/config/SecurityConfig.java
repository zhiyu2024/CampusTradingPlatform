package cn.gdsdxy.campustrading.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * 密码加密器（BCrypt）
     * 强度越高，加密越慢但更安全
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 推荐10-12
    }
}