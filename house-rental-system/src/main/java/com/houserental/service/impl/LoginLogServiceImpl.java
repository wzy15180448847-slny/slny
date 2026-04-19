package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.houserental.dto.LoginLogDTO;
import com.houserental.entity.LoginLog;
import com.houserental.mapper.LoginLogMapper;
import com.houserental.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 登录日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    /**
     * 登录失败次数限制（24小时内）
     */
    @Value("${app.login.failure.limit:5}")
    private int loginFailureLimit;

    @Override
    public void recordLoginSuccess(String username, String ip, String userAgent) {
        LoginLog loginLog = createLoginLog(username, ip, userAgent, 1, null);
        saveLoginLog(loginLog);
    }

    @Override
    public void recordLoginFailure(String username, String ip, String userAgent, String failReason) {
        LoginLog loginLog = createLoginLog(username, ip, userAgent, 0, failReason);
        saveLoginLog(loginLog);
    }

    @Override
    public boolean checkLoginFailureLimit(String username, String ip) {
        try {
            // 检查用户登录失败次数
            int userFailureCount = loginLogMapper.countLoginFailuresByUsername(username);
            if (userFailureCount >= loginFailureLimit) {
                log.warn("用户 {} 登录失败次数超过限制: {}次", username, userFailureCount);
                return true;
            }

            // 检查IP登录失败次数
            int ipFailureCount = loginLogMapper.countLoginFailuresByIp(ip);
            if (ipFailureCount >= loginFailureLimit * 2) { // IP限制更严格
                log.warn("IP {} 登录失败次数超过限制: {}次", ip, ipFailureCount);
                return true;
            }
        } catch (Exception e) {
            log.warn("检查登录失败次数时发生异常: {}", e.getMessage());
            // 如果表不存在或其他异常，不限制登录
        }

        return false;
    }

    @Override
    public void saveLoginLog(LoginLog loginLog) {
        try {
            loginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.warn("保存登录日志时发生异常: {}", e.getMessage());
            // 如果表不存在或其他异常，忽略日志保存
        }
    }

    /**
     * 创建登录日志对象
     */
    private LoginLog createLoginLog(String username, String ip, String userAgent, int loginResult, String failReason) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setIpAddress(ip);
        loginLog.setLoginLocation(getLocationFromIp(ip));
        loginLog.setLoginDevice(parseUserAgent(userAgent));
        loginLog.setStatus(loginResult);
        loginLog.setFailReason(failReason);
        return loginLog;
    }

    /**
     * 解析用户代理字符串
     */
    private String parseUserAgent(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return "Unknown";
        }

        // 简单的用户代理解析，实际项目中可以使用更复杂的解析库
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Mobile")) {
            return "Mobile Device";
        } else {
            return "Other";
        }
    }

    /**
     * 从IP地址获取位置信息
     */
    private String getLocationFromIp(String ip) {
        // 这里可以集成IP地址解析服务，如百度地图API、高德地图API等
        // 目前返回默认值
        return "Unknown Location";
    }

    /**
     * 从HttpServletRequest中获取真实IP地址
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    @Override
    public List<LoginLogDTO> getRecentLogs(int limit) {
        QueryWrapper<LoginLog> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0).orderByDesc("create_time").last("LIMIT " + limit);
        
        List<LoginLog> logs = loginLogMapper.selectList(wrapper);
        
        return logs.stream().map(log -> {
            LoginLogDTO dto = new LoginLogDTO();
            dto.setId(log.getId());
            dto.setUsername(log.getUsername());
            dto.setLoginIp(log.getIpAddress());
            dto.setLoginLocation(log.getLoginLocation());
            dto.setLoginDevice(log.getLoginDevice());
            dto.setLoginResult(log.getStatus());
            dto.setFailReason(log.getFailReason());
            dto.setCreateTime(log.getCreateTime());
            return dto;
        }).collect(Collectors.toList());
    }

}
