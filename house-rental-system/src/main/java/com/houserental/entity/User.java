package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@TableName("sys_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @TableField(value = "username", exist = true)
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password", exist = true)
    private String password;

    /**
     * 昵称
     */
    @TableField(value = "nickname", exist = true)
    private String nickname;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name", exist = true)
    private String realName;

    /**
     * 手机号
     */
    @TableField(value = "phone", exist = true)
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email", exist = true)
    private String email;

    /**
     * 头像URL
     */
    @TableField(value = "avatar", exist = true)
    private String avatar;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    @TableField(value = "gender", exist = true)
    private Integer gender;

    /**
     * 身份证号
     */
    @TableField(value = "id_card", exist = true)
    private String idCard;

    /**
     * 用户类型（LANDLORD-房东, TENANT-租客, AGENT-中介, ADMIN-管理员）
     */
    @TableField(value = "user_type", exist = true)
    private String userType;

    /**
     * 状态（0-禁用，1-启用，2-锁定）
     */
    @TableField(value = "status", exist = true)
    private Integer status = 1;

    /**
     * 最后登录时间
     */
    @TableField(value = "last_login_time", exist = true)
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @TableField(value = "last_login_ip", exist = true)
    private String lastLoginIp;

    /**
     * 登录失败次数
     */
    @TableField(value = "login_fail_count", exist = true)
    private Integer loginFailCount = 0;

    /**
     * 密码最后修改时间
     */
    @TableField(value = "password_update_time", exist = true)
    private LocalDateTime passwordUpdateTime;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Integer getLoginFailCount() {
        return loginFailCount;
    }

    public void setLoginFailCount(Integer loginFailCount) {
        this.loginFailCount = loginFailCount;
    }

    public LocalDateTime getPasswordUpdateTime() {
        return passwordUpdateTime;
    }

    public void setPasswordUpdateTime(LocalDateTime passwordUpdateTime) {
        this.passwordUpdateTime = passwordUpdateTime;
    }
}
