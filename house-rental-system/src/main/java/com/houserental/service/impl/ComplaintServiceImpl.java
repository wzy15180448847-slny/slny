package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.Complaint;
import com.houserental.entity.User;
import com.houserental.mapper.ComplaintMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户投诉服务实现
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {

    @Autowired
    private ComplaintMapper complaintMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public boolean submitComplaint(Complaint complaint) {
        // 检查投诉人和被投诉人是否存在
        User complainant = userMapper.selectById(complaint.getComplainantId());
        User respondent = userMapper.selectById(complaint.getRespondentId());
        if (complainant == null || respondent == null) {
            return false;
        }

        // 设置默认值
        complaint.setStatus(0);
        complaint.setCreateTime(LocalDateTime.now());
        complaint.setUpdateTime(LocalDateTime.now());

        return save(complaint);
    }

    @Override
    @Transactional
    public boolean processComplaint(Long id, Integer status, String processResult, Long processorId) {
        Complaint complaint = getById(id);
        if (complaint == null || complaint.getStatus() == 2 || complaint.getStatus() == 3) {
            return false;
        }

        // 更新处理状态
        complaint.setStatus(status);
        complaint.setProcessResult(processResult);
        complaint.setProcessorId(processorId);
        complaint.setProcessTime(LocalDateTime.now());
        complaint.setUpdateTime(LocalDateTime.now());

        return updateById(complaint);
    }

    @Override
    public Complaint getById(Long id) {
        return complaintMapper.selectById(id);
    }

    @Override
    public PageResult<Complaint> getByComplainantId(Long complainantId, int page, int size) {
        Page<Complaint> pageInfo = new Page<>(page, size);
        QueryWrapper<Complaint> wrapper = new QueryWrapper<>();
        wrapper.eq("complainant_id", complainantId).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }

    @Override
    public PageResult<Complaint> getByRespondentId(Long respondentId, int page, int size) {
        Page<Complaint> pageInfo = new Page<>(page, size);
        QueryWrapper<Complaint> wrapper = new QueryWrapper<>();
        wrapper.eq("respondent_id", respondentId).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }

    @Override
    public PageResult<Complaint> getPendingList(int page, int size) {
        Page<Complaint> pageInfo = new Page<>(page, size);
        QueryWrapper<Complaint> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }

    @Override
    public PageResult<Complaint> getProcessingList(int page, int size) {
        Page<Complaint> pageInfo = new Page<>(page, size);
        QueryWrapper<Complaint> wrapper = new QueryWrapper<>();
        wrapper.in("status", 2, 3).orderByDesc("process_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }
}
