package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.LeaseAgreement;
import com.houserental.entity.TerminationApplication;
import com.houserental.entity.User;
import com.houserental.entity.House;
import com.houserental.mapper.HouseMapper;
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
    private HouseMapper houseMapper;

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
        application.setCompensation(penaltyAmount);
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
        // 设置默认分页参数
        long pageNum = params.containsKey("page") ? Long.parseLong(params.get("page").toString()) : 1;
        long pageSize = params.containsKey("size") ? Long.parseLong(params.get("size").toString()) : 10;
        Page<TerminationApplication> page = new Page<>(pageNum, pageSize);
        QueryWrapper<TerminationApplication> wrapper = new QueryWrapper<>();

        // 添加查询条件
        if (params.containsKey("leaseId")) {
            wrapper.eq("agreement_id", params.get("leaseId"));
        }
        if (params.containsKey("applicantId")) {
            wrapper.eq("applicant_id", params.get("applicantId"));
        }
        if (params.containsKey("processorId")) {
            wrapper.eq("processor_id", params.get("processorId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq("status", params.get("status"));
        }

        // 分页查询
        baseMapper.selectPage(page, wrapper);
        
        // 如果是房东查询，过滤出属于该房东的解约申请
        if (params.containsKey("landlordId")) {
            Long landlordId = Long.parseLong(params.get("landlordId").toString());
            if (page.getRecords() != null && !page.getRecords().isEmpty()) {
                page.getRecords().removeIf(app -> {
                    if (app == null || app.getLeaseId() == null) {
                        return true;
                    }
                    LeaseAgreement lease = leaseAgreementMapper.selectById(app.getLeaseId());
                    return lease == null || lease.getLandlordId() == null || !landlordId.equals(lease.getLandlordId());
                });
            }
        }

        // 填充关联数据
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            for (TerminationApplication application : page.getRecords()) {
                fillApplicationDetails(application);
            }
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
            if (lease != null) {
                // 填充房源信息
                if (lease.getHouseId() != null) {
                    House house = houseMapper.selectById(lease.getHouseId());
                    lease.setHouse(house);
                }
                application.setLease(lease);
            }
        }
        // 填充申请人信息
        if (application.getApplicantId() != null) {
            User applicant = userMapper.selectById(application.getApplicantId());
            application.setApplicant(applicant);
        }
    }
}