package com.houserental.service;

import com.houserental.dto.LoginRequest;
import com.houserental.dto.LoginResponse;
import com.houserental.dto.RegisterRequest;
import com.houserental.dto.UpdatePasswordRequest;
import com.houserental.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 根据ID查询用户
     */
    User getById(Long id);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 更新用户信息
     */
    User update(User user);

    /**
     * 修改密码
     */
    void updatePassword(UpdatePasswordRequest request);

    /**
     * 重置密码
     */
    void resetPassword(String phone, String newPassword);

    /**
     * 禁用用户
     */
    void disableUser(Long userId);

    /**
     * 启用用户
     */
    void enableUser(Long userId);

    /**
     * 锁定用户
     */
    void lockUser(Long userId);

    /**
     * 解锁用户
     */
    void unlockUser(Long userId);

    /**
     * 根据用户类型查询用户列表
     */
    java.util.List<User> listByUserType(String userType);

    /**
     * 保存用户
     */
    void save(User user);
}
