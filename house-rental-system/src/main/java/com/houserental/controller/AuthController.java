package com.houserental.controller;

import com.houserental.common.dto.UserContext;
import com.houserental.common.result.Result;
import com.houserental.dto.LoginRequest;
import com.houserental.dto.LoginResponse;
import com.houserental.dto.RegisterRequest;
import com.houserental.dto.ResetPasswordRequest;
import com.houserental.dto.UpdatePasswordRequest;
import com.houserental.entity.User;
import com.houserental.mapper.UserMapper;
import com.houserental.service.UserService;
import com.houserental.service.VerifyCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final VerifyCodeService verifyCodeService;

    @PostMapping("/register")
    public Result<User> register(@Validated @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request, 
                                       HttpServletRequest httpRequest) {
        request.setIp(com.houserental.service.impl.LoginLogServiceImpl.getRealIp(httpRequest));
        request.setUserAgent(httpRequest.getHeader("User-Agent"));
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        return Result.success();
    }

    @GetMapping("/info")
    public Result<UserContext> getUserInfo() {
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        User user = userService.getById(userId);
        java.util.List<String> roles = userMapper.selectRolesByUserId(userId);
        java.util.List<String> permissions = userMapper.selectPermissionsByUserId(userId);
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
                .roles(new HashSet<>(roles))
                .permissions(new HashSet<>(permissions))
                .build();
        return Result.success(userContext);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return Result.success();
    }

    @PutMapping("/profile")
    public Result<UserContext> updateProfile(@RequestBody User user) {
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        user.setId(userId);
        User updatedUser = userService.update(user);
        java.util.List<String> roles = userMapper.selectRolesByUserId(userId);
        java.util.List<String> permissions = userMapper.selectPermissionsByUserId(userId);
        UserContext userContext = UserContext.builder()
                .userId(updatedUser.getId())
                .username(updatedUser.getUsername())
                .nickname(updatedUser.getNickname())
                .realName(updatedUser.getRealName())
                .phone(updatedUser.getPhone())
                .email(updatedUser.getEmail())
                .avatar(updatedUser.getAvatar())
                .userType(updatedUser.getUserType())
                .status(updatedUser.getStatus())
                .roles(new java.util.HashSet<>(roles))
                .permissions(new java.util.HashSet<>(permissions))
                .build();
        return Result.success(userContext);
    }

    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestParam("phone") String phone) {
        String code = verifyCodeService.generateCode(phone);
        verifyCodeService.sendCode(phone, code);
        return Result.success();
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        // 验证验证码
        boolean isValid = verifyCodeService.verifyCode(request.getPhone(), request.getCode());
        if (!isValid) {
            return Result.error("验证码错误或已过期");
        }
        // 重置密码
        userService.resetPassword(request.getPhone(), request.getNewPassword());
        return Result.success();
    }
}
