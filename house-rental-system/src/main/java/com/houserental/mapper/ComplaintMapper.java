package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.Complaint;

/**
 * 用户投诉Mapper
 */
public interface ComplaintMapper extends BaseMapper<Complaint> {

    /**
     * 根据投诉人ID查询投诉列表
     * @param complainantId 投诉人ID
     * @return 投诉列表
     */
    java.util.List<Complaint> selectByComplainantId(Long complainantId);

    /**
     * 根据被投诉人ID查询投诉列表
     * @param respondentId 被投诉人ID
     * @return 投诉列表
     */
    java.util.List<Complaint> selectByRespondentId(Long respondentId);

    /**
     * 根据处理状态查询投诉列表
     * @param status 处理状态
     * @return 投诉列表
     */
    java.util.List<Complaint> selectByStatus(Integer status);
}
