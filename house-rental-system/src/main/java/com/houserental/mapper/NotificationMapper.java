package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内消息Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
