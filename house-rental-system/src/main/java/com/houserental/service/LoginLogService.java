package com.houserental.service;

import com.houserental.dto.LoginLogDTO;
import com.houserental.entity.LoginLog;

import java.util.List;
import java.util.Map;

/**
 * 登录日志服务
 */
public interface LoginLogService {

    /**
     * 记录登录成功
     */
    void recordLoginSuccess(String username, String ip, String userAgent);

    /**
     * 记录登录失败
     */
    void recordLoginFailure(String username, String ip, String userAgent, String failReason);

    /**
     * 检查登录失败次数是否超过限制
     */
    boolean checkLoginFailureLimit(String username, String ip);

    /**
     * 保存登录日志
     */
    void saveLoginLog(LoginLog loginLog);

    /**
     * 获取最近的登录日志
     */
    List<LoginLogDTO> getRecentLogs(int limit);

    /**
     * 根据条件查询登录日志
     */
    List<LoginLogDTO> getLogsByCondition(String keyword, String type, String date);

    /**
     * 获取今日登录统计
     */
    Map<String, Long> getTodayLoginStats();

}
