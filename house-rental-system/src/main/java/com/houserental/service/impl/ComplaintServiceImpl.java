package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.Complaint;
import com.houserental.entity.User;
import com.houserental.mapper.ComplaintMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.exception.BusinessException;
import com.houserental.common.result.PageResult;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(complaint.getComplainantId())) {
            throw new BusinessException("不能冒充他人进行投诉");
        }

        User complainant = userMapper.selectById(complaint.getComplainantId());
        User respondent = userMapper.selectById(complaint.getRespondentId());
        if (complainant == null || respondent == null) {
            return false;
        }

        complaint.setStatus(0);
        complaint.setCreateTime(LocalDateTime.now());
        complaint.setUpdateTime(LocalDateTime.now());

        return save(complaint);
    }

    @Override
    @Transactional
    public boolean processComplaint(Long id, Integer status, String processResult, Long processorId) {
        Complaint complaint = complaintMapper.selectById(id);
        if (complaint == null || complaint.getStatus() == 2 || complaint.getStatus() == 3) {
            return false;
        }

        complaint.setStatus(status);
        complaint.setProcessResult(processResult);
        complaint.setUpdateTime(LocalDateTime.now());

        return complaintMapper.updateById(complaint) > 0;
    }

    @Override
    public Complaint getById(Long id) {
        return complaintMapper.selectById(id);
    }

    @Override
    public PageResult<Complaint> getByComplainantId(Long complainantId, int page, int size) {
        List<Complaint> records = complaintMapper.selectByComplainantId(complainantId);
        return PageResult.build((long) page, (long) size, (long) records.size(), records);
    }

    @Override
    public PageResult<Complaint> getByRespondentId(Long respondentId, int page, int size) {
        List<Complaint> records = complaintMapper.selectByRespondentId(respondentId);
        return PageResult.build((long) page, (long) size, (long) records.size(), records);
    }

    @Override
    public PageResult<Complaint> getPendingList(int page, int size) {
        Long offset = (long) (page - 1) * size;
        List<Complaint> records = complaintMapper.selectPendingList(offset, (long) size);
        Long total = complaintMapper.selectPendingCount();
        return PageResult.build((long) page, (long) size, total, records);
    }

    @Override
    public PageResult<Complaint> getProcessingList(int page, int size) {
        Long offset = (long) (page - 1) * size;
        List<Complaint> records = complaintMapper.selectHandledList(offset, (long) size);
        Long total = complaintMapper.selectHandledCount();
        return PageResult.build((long) page, (long) size, total, records);
    }
}
