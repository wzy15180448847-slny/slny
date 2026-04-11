package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.RentReminder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租金催缴Mapper
 */
@Mapper
public interface RentReminderMapper extends BaseMapper<RentReminder> {
}
