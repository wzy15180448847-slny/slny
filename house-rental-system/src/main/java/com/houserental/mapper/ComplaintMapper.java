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

    /**
     * 查询待处理投诉列表（分页）
     * @param offset 偏移量
     * @param size 每页大小
     * @return 投诉列表
     */
    java.util.List<Complaint> selectPendingList(Long offset, Long size);

    /**
     * 查询已处理投诉列表（分页）
     * @param offset 偏移量
     * @param size 每页大小
     * @return 投诉列表
     */
    java.util.List<Complaint> selectHandledList(Long offset, Long size);

    /**
     * 查询待处理投诉数量
     * @return 数量
     */
    Long selectPendingCount();

    /**
     * 查询已处理投诉数量
     * @return 数量
     */
    Long selectHandledCount();

    /**
     * 根据ID查询投诉详情
     * @param id 投诉ID
     * @return 投诉详情
     */
    Complaint selectById(Long id);
}
