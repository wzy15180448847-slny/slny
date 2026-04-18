package com.houserental.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtils {

    /**
     * 密钥（从环境变量或配置文件中读取）
     */
    private static final String SECRET = System.getenv().getOrDefault("JWT_SECRET", 
        "house-rental-platform-secret-key-2024-secure-jwt-token");

    /**
     * 默认过期时间（24小时）
     */
    private static final long DEFAULT_EXPIRATION = 86400000L;

    /**
     * 刷新令牌过期时间（7天）
     */
    private static final long REFRESH_EXPIRATION = 604800000L;

    /**
     * 生成密钥
     */
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public static String generateToken(Long userId, String username) {
        return generateToken(userId, username, null, DEFAULT_EXPIRATION);
    }

    /**
     * 生成访问令牌（带额外声明）
     *
     * @param userId    用户ID
     * @param username  用户名
     * @param claims    额外声明
     * @return JWT令牌
     */
    public static String generateToken(Long userId, String username, Map<String, Object> claims) {
        return generateToken(userId, username, claims, DEFAULT_EXPIRATION);
    }

    /**
     * 生成访问令牌（带角色和权限）
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param roles       角色列表
     * @param permissions 权限列表
     * @return JWT令牌
     */
    public static String generateToken(Long userId, String username, List<String> roles, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        return generateToken(userId, username, claims, DEFAULT_EXPIRATION);
    }

    /**
     * 生成令牌
     *
     * @param userId     用户ID
     * @param username   用户名
     * @param claims     额外声明
     * @param expiration 过期时间（毫秒）
     * @return JWT令牌
     */
    public static String generateToken(Long userId, String username, Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("userId", userId);
        tokenClaims.put("username", username);
        if (claims != null) {
            tokenClaims.putAll(claims);
        }

        return Jwts.builder()
                .setClaims(tokenClaims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成刷新令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 刷新令牌
     */
    public static String generateRefreshToken(Long userId, String username) {
        return generateToken(userId, username, null, REFRESH_EXPIRATION);
    }

    /**
     * 解析令牌
     *
     * @param token JWT令牌
     * @return 声明
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT令牌: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("JWT令牌格式错误: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("JWT签名验证失败: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT令牌为空或非法: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 验证令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username").toString();
    }

    /**
     * 获取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public static Date getExpirationDate(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 检查令牌是否即将过期
     *
     * @param token     JWT令牌
     * @param threshold 阈值（毫秒）
     * @return 是否即将过期
     */
    public static boolean isTokenExpiredSoon(String token, long threshold) {
        Date expiration = getExpirationDate(token);
        return expiration.getTime() - System.currentTimeMillis() < threshold;
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public static String refreshToken(String token) {
        Claims claims = parseToken(token);
        Long userId = Long.valueOf(claims.get("userId").toString());
        String username = claims.get("username").toString();
        return generateToken(userId, username);
    }
}
