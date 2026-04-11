package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.Complaint;
import com.houserental.common.result.PageResult;

/**
 * 用户投诉服务
 */
public interface ComplaintService extends IService<Complaint> {

    /**
     * 提交投诉
     * @param complaint 投诉信息
     * @return 提交结果
     */
    boolean submitComplaint(Complaint complaint);

    /**
     * 处理投诉
     * @param id 投诉ID
     * @param status 处理状态
     * @param processResult 处理结果
     * @param processorId 处理人ID
     * @return 处理结果
     */
    boolean processComplaint(Long id, Integer status, String processResult, Long processorId);

    /**
     * 根据ID查询投诉详情
     * @param id 投诉ID
     * @return 投诉详情
     */
    Complaint getById(Long id);

    /**
     * 查询投诉人自己的投诉列表
     * @param complainantId 投诉人ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<Complaint> getByComplainantId(Long complainantId, int page, int size);

    /**
     * 查询被投诉人的投诉列表
     * @param respondentId 被投诉人ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<Complaint> getByRespondentId(Long respondentId, int page, int size);

    /**
     * 查询待处理的投诉列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<Complaint> getPendingList(int page, int size);

    /**
     * 查询处理中的投诉列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<Complaint> getProcessingList(int page, int size);
}
