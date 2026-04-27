package com.houserental.service.impl;

import com.houserental.common.dto.UserContext;
import com.houserental.common.exception.BusinessException;
import com.houserental.common.result.ResultCode;
import com.houserental.common.utils.JwtUtils;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.dto.LoginRequest;
import com.houserental.dto.LoginResponse;
import com.houserental.dto.RegisterRequest;
import com.houserental.dto.UpdatePasswordRequest;
import com.houserental.entity.User;
import com.houserental.mapper.UserMapper;
import com.houserental.service.LoginLogService;
import com.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LoginLogService loginLogService;

    private static final String TOKEN_PREFIX = "token:";
    private static final int MAX_LOGIN_FAIL_COUNT = 5;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterRequest request) {
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        if (userMapper.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setNickname(request.getUsername());
        user.setUserType(request.getUserType());
        user.setStatus(1);
        user.setLoginFailCount(0);
        user.setPasswordUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        // 检查登录失败次数限制
        if (loginLogService.checkLoginFailureLimit(request.getUsername(), request.getIp())) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_LOCKED);
        }

        User user = userMapper.selectByUsernameOrPhone(request.getUsername());
        if (user == null) {
            loginLogService.recordLoginFailure(request.getUsername(), request.getIp(), request.getUserAgent(), "用户不存在");
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() == 0) {
            loginLogService.recordLoginFailure(request.getUsername(), request.getIp(), request.getUserAgent(), "账户已禁用");
            throw new BusinessException(ResultCode.USER_ACCOUNT_DISABLED);
        }

        if (user.getStatus() == 2) {
            loginLogService.recordLoginFailure(request.getUsername(), request.getIp(), request.getUserAgent(), "账户已锁定");
            throw new BusinessException(ResultCode.USER_ACCOUNT_LOCKED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleLoginFail(user);
            loginLogService.recordLoginFailure(request.getUsername(), request.getIp(), request.getUserAgent(), "密码错误");
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        if (user.getLoginFailCount() != null && user.getLoginFailCount() > 0) {
            user.setLoginFailCount(0);
        }

        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(request.getIp());
        userMapper.updateById(user);

        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        if (roles == null) {
            roles = java.util.Collections.emptyList();
        }

        List<String> permissions = userMapper.selectPermissionsByUserId(user.getId());
        if (permissions == null) {
            permissions = java.util.Collections.emptyList();
        }

        // 生成包含角色和权限信息的JWT令牌
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), roles, permissions);
        String refreshToken = JwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        // 缓存用户信息和令牌
        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + user.getId(),
                token,
                24,
                TimeUnit.HOURS
        );
        
        // 缓存用户角色和权限信息，有效期1小时
        redisTemplate.opsForValue().set(
                "user:roles:" + user.getId(),
                roles,
                1,
                TimeUnit.HOURS
        );
        redisTemplate.opsForValue().set(
                "user:permissions:" + user.getId(),
                permissions,
                1,
                TimeUnit.HOURS
        );

        UserContext userContext = UserContext.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .userType(user.getUserType())
                .status(user.getStatus())
                .gender(user.getGender())
                .creditScore(user.getCreditScore())
                .lastLoginTime(user.getLastLoginTime() != null ? user.getLastLoginTime().toString() : null)
                .lastLoginIp(user.getLastLoginIp())
                .roles(new HashSet<>(roles))
                .permissions(new HashSet<>(permissions))
                .build();

        // 记录登录成功日志
        loginLogService.recordLoginSuccess(user.getUsername(), request.getIp(), request.getUserAgent());

        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userContext)
                .build();
    }

    @Override
    public void logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            redisTemplate.delete(TOKEN_PREFIX + userId);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getById(Long id) {
        String cacheKey = "user:id:" + id;
        User user = (User) redisTemplate.opsForValue().get(cacheKey);
        if (user != null) {
            return user;
        }
        
        user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 缓存用户信息，有效期30分钟
        redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        return user;
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getByUsername(String username) {
        String cacheKey = "user:username:" + username;
        User user = (User) redisTemplate.opsForValue().get(cacheKey);
        if (user != null) {
            return user;
        }
        
        user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 缓存用户信息，有效期30分钟
        redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User update(User user) {
        User existUser = getById(user.getId());
        
        if (user.getNickname() != null) {
            existUser.setNickname(user.getNickname());
        }
        if (user.getEmail() != null) {
            existUser.setEmail(user.getEmail());
        }
        if (user.getAvatar() != null) {
            existUser.setAvatar(user.getAvatar());
        }
        if (user.getRealName() != null) {
            existUser.setRealName(user.getRealName());
        }
        if (user.getIdCard() != null) {
            existUser.setIdCard(user.getIdCard());
        }
        if (user.getGender() != null) {
            existUser.setGender(user.getGender());
        }
        if (user.getPhone() != null) {
            existUser.setPhone(user.getPhone());
        }
        if (user.getUserType() != null) {
            existUser.setUserType(user.getUserType());
        }

        userMapper.updateById(existUser);
        
        // 清理缓存
        redisTemplate.delete("user:id:" + user.getId());
        redisTemplate.delete("user:username:" + existUser.getUsername());
        
        return existUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = getById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_OLD_PASSWORD_ERROR);
        }

        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_SAME);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 清理缓存
        redisTemplate.delete(TOKEN_PREFIX + userId);
        redisTemplate.delete("user:id:" + userId);
        redisTemplate.delete("user:username:" + user.getUsername());
        redisTemplate.delete("user:roles:" + userId);
        redisTemplate.delete("user:permissions:" + userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String phone, String newPassword) {
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordUpdateTime(LocalDateTime.now());
        user.setLoginFailCount(0);
        user.setStatus(1);
        userMapper.updateById(user);

        // 清理缓存
        redisTemplate.delete(TOKEN_PREFIX + user.getId());
        redisTemplate.delete("user:id:" + user.getId());
        redisTemplate.delete("user:username:" + user.getUsername());
        redisTemplate.delete("user:roles:" + user.getId());
        redisTemplate.delete("user:permissions:" + user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableUser(Long userId) {
        User user = getById(userId);
        user.setStatus(0);
        userMapper.updateById(user);
        
        // 清理缓存
        redisTemplate.delete(TOKEN_PREFIX + userId);
        redisTemplate.delete("user:id:" + userId);
        redisTemplate.delete("user:username:" + user.getUsername());
        redisTemplate.delete("user:roles:" + userId);
        redisTemplate.delete("user:permissions:" + userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableUser(Long userId) {
        User user = getById(userId);
        user.setStatus(1);
        userMapper.updateById(user);
        
        // 清理缓存
        redisTemplate.delete("user:id:" + userId);
        redisTemplate.delete("user:username:" + user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockUser(Long userId) {
        User user = getById(userId);
        user.setStatus(2);
        userMapper.updateById(user);
        
        // 清理缓存
        redisTemplate.delete(TOKEN_PREFIX + userId);
        redisTemplate.delete("user:id:" + userId);
        redisTemplate.delete("user:username:" + user.getUsername());
        redisTemplate.delete("user:roles:" + userId);
        redisTemplate.delete("user:permissions:" + userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockUser(Long userId) {
        User user = getById(userId);
        user.setStatus(1);
        user.setLoginFailCount(0);
        userMapper.updateById(user);
        
        // 清理缓存
        redisTemplate.delete("user:id:" + userId);
        redisTemplate.delete("user:username:" + user.getUsername());
    }

    @Override
    public java.util.List<User> listByUserType(String userType) {
        return userMapper.selectByUserType(userType);
    }

    @Override
    public void save(User user) {
        userMapper.insert(user);
    }

    private void handleLoginFail(User user) {
        int failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
        user.setLoginFailCount(failCount);

        if (failCount >= MAX_LOGIN_FAIL_COUNT) {
            user.setStatus(2);
            log.warn("用户 {} 登录失败次数过多，已锁定", user.getUsername());
        }

        userMapper.updateById(user);
    }
}
