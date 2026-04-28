package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.LeaseAgreement;
import com.houserental.entity.TerminationApplication;
import com.houserental.entity.User;
import com.houserental.mapper.LeaseAgreementMapper;
import com.houserental.mapper.TerminationApplicationMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.exception.BusinessException;
import com.houserental.common.result.PageResult;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.service.LeaseAgreementService;
import com.houserental.service.TerminationApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 解约申请服务实现
 */
@Service
public class TerminationApplicationServiceImpl extends ServiceImpl<TerminationApplicationMapper, TerminationApplication> implements TerminationApplicationService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Override
    public Long createApplication(TerminationApplication application) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        application.setApplicantId(currentUserId);

        LeaseAgreement lease = leaseAgreementMapper.selectById(application.getLeaseId());
        if (lease == null) {
            throw new BusinessException("租约不存在");
        }

        if (!currentUserId.equals(lease.getTenantId()) && !currentUserId.equals(lease.getLandlordId())) {
            throw new BusinessException("无权对他人合同申请解约");
        }

        application.setApplicationNo("TERM" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        application.setApplyTime(new java.util.Date());
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(application);
        return application.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processApplication(Long id, Integer status, String processingOpinion, BigDecimal penaltyAmount) {
        TerminationApplication application = baseMapper.selectById(id);
        if (application == null) {
            return false;
        }

        application.setStatus(status);
        application.setProcessingTime(new java.util.Date());
        application.setProcessingOpinion(processingOpinion);
        application.setPenaltyAmount(penaltyAmount);
        application.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(application);

        if (status == 1) {
            leaseAgreementService.terminateLease(application.getLeaseId(), "解约申请已通过");
        }

        return true;
    }

    @Override
    public boolean completeTermination(Long id) {
        TerminationApplication application = baseMapper.selectById(id);
        if (application == null) {
            return false;
        }
        application.setStatus(3); // 已完成
        application.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(application) > 0;
    }

    @Override
    public BigDecimal calculatePenalty(Long leaseId, String terminationReason) {
        // 这里实现违约金计算逻辑
        // 实际项目中可能需要根据租约条款、剩余租期等因素计算
        LeaseAgreement lease = leaseAgreementMapper.selectById(leaseId);
        if (lease == null) {
            return BigDecimal.ZERO;
        }

        // 简单计算：按一个月租金作为违约金
        return lease.getRentPrice();
    }

    @Override
    public PageResult<TerminationApplication> pageApplications(Map<String, Object> params) {
        Page<TerminationApplication> page = new Page<>(Long.parseLong(params.get("page").toString()), Long.parseLong(params.get("size").toString()));
        QueryWrapper<TerminationApplication> wrapper = new QueryWrapper<>();

        // 添加查询条件
        if (params.containsKey("leaseId")) {
            wrapper.eq("agreement_id", params.get("leaseId"));
        }
        if (params.containsKey("applicantId")) {
            wrapper.eq("applicant_id", params.get("applicantId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq("status", params.get("status"));
        }

        // 分页查询
        baseMapper.selectPage(page, wrapper);

        // 填充关联数据
        for (TerminationApplication application : page.getRecords()) {
            fillApplicationDetails(application);
        }

        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    public TerminationApplication getApplicationById(Long id) {
        TerminationApplication application = baseMapper.selectById(id);
        if (application != null) {
            fillApplicationDetails(application);
        }
        return application;
    }

    private void fillApplicationDetails(TerminationApplication application) {
        // 填充租约信息
        if (application.getLeaseId() != null) {
            LeaseAgreement lease = leaseAgreementMapper.selectById(application.getLeaseId());
            application.setLease(lease);
        }
        // 填充申请人信息
        if (application.getApplicantId() != null) {
            User applicant = userMapper.selectById(application.getApplicantId());
            application.setApplicant(applicant);
        }
        // 填充处理人信息
        if (application.getProcessorId() != null) {
            User processor = userMapper.selectById(application.getProcessorId());
            application.setProcessor(processor);
        }
    }
}