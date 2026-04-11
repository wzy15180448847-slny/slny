package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 登录日志实体
 */
@TableName("sys_login_log")
public class LoginLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @TableField(value = "username", exist = true)
    private String username;

    /**
     * 登录IP
     */
    @TableField(value = "login_ip", exist = true)
    private String loginIp;

    /**
     * 登录地点
     */
    @TableField(value = "login_location", exist = true)
    private String loginLocation;

    /**
     * 登录设备
     */
    @TableField(value = "login_device", exist = true)
    private String loginDevice;

    /**
     * 登录结果（0-失败，1-成功）
     */
    @TableField(value = "login_result", exist = true)
    private Integer loginResult;

    /**
     * 失败原因
     */
    @TableField(value = "fail_reason", exist = true)
    private String failReason;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getLoginDevice() {
        return loginDevice;
    }

    public void setLoginDevice(String loginDevice) {
        this.loginDevice = loginDevice;
    }

    public Integer getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(Integer loginResult) {
        this.loginResult = loginResult;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
