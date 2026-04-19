package com.houserental.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志DTO
 */
@Data
public class LoginLogDTO {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 登录设备
     */
    private String loginDevice;

    /**
     * 登录结果（0-失败，1-成功）
     */
    private Integer loginResult;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 登录时间
     */
    private LocalDateTime createTime;
}