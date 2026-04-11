package com.houserental.common.utils;

import com.houserental.common.dto.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 */
public class SecurityUtils {

    /**
     * 获取当前用户上下文
     *
     * @return 用户上下文
     */
    public static UserContext getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserContext) {
            return (UserContext) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        UserContext userContext = getCurrentUser();
        return userContext != null ? userContext.getUserId() : null;
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        UserContext userContext = getCurrentUser();
        return userContext != null ? userContext.getUsername() : null;
    }

    /**
     * 检查是否已认证
     *
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 检查当前用户是否有指定角色
     *
     * @param role 角色
     * @return 是否有该角色
     */
    public static boolean hasRole(String role) {
        UserContext userContext = getCurrentUser();
        if (userContext == null) {
            return false;
        }
        return userContext.getRoles().contains(role);
    }

    /**
     * 检查当前用户是否有任意指定角色
     *
     * @param roles 角色列表
     * @return 是否有任意角色
     */
    public static boolean hasAnyRole(String... roles) {
        UserContext userContext = getCurrentUser();
        if (userContext == null) {
            return false;
        }
        for (String role : roles) {
            if (userContext.getRoles().contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否有指定权限
     *
     * @param permission 权限
     * @return 是否有该权限
     */
    public static boolean hasPermission(String permission) {
        UserContext userContext = getCurrentUser();
        if (userContext == null) {
            return false;
        }
        return userContext.getPermissions().contains(permission);
    }
}
