package cn.gdsdxy.campustrading.common.util;

import cn.gdsdxy.campustrading.common.model.LoginUser; // 需创建此类
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * 获取当前登录用户的ID
     * 前提：JwtFilter 在认证成功后，将 LoginUser 对象放入 Authentication
     */
    public static Long getUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("用户未登录");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return ((LoginUser) principal).getUserId(); // ✅ 正确获取
            }

            throw new RuntimeException("无法识别用户身份信息");
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户的用户名
     */
    public static String getUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return ((LoginUser) principal).getUsername();
            }

            return principal.toString();
        } catch (Exception e) {
            return null;
        }
    }
}