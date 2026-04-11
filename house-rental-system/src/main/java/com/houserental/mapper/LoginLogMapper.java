package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.LoginLog;
import org.springframework.stereotype.Repository;

/**
 * 登录日志数据访问层
 */
@Repository
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 统计指定用户的登录失败次数（24小时内）
     */
    int countLoginFailuresByUsername(String username);

    /**
     * 统计指定IP的登录失败次数（24小时内）
     */
    int countLoginFailuresByIp(String ip);

}
