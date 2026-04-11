package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.MessageRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息发送记录Mapper
 */
@Mapper
public interface MessageRecordMapper extends BaseMapper<MessageRecord> {
}
