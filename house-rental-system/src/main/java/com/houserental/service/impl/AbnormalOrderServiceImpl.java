package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.AbnormalOrder;
import com.houserental.mapper.AbnormalOrderMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.AbnormalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 异常订单服务实现
 */
@Service
public class AbnormalOrderServiceImpl extends ServiceImpl<AbnormalOrderMapper, AbnormalOrder> implements AbnormalOrderService {

    @Autowired
    private AbnormalOrderMapper abnormalOrderMapper;

    @Override
    @Transactional
    public boolean submitAbnormalOrder(AbnormalOrder abnormalOrder) {
        // 检查是否已存在相同订单的异常记录
        AbnormalOrder existing = abnormalOrderMapper.selectByOrderTypeAndId(
                abnormalOrder.getOrderType(), abnormalOrder.getOrderId());
        if (existing != null && existing.getStatus() < 2) {
            return false;
        }

        // 设置默认值
        abnormalOrder.setStatus(0);
        abnormalOrder.setCreateTime(LocalDateTime.now());
        abnormalOrder.setUpdateTime(LocalDateTime.now());

        return save(abnormalOrder);
    }

    @Override
    @Transactional
    public boolean processAbnormalOrder(Long id, Integer status, String processPlan, Long processorId) {
        AbnormalOrder abnormalOrder = getById(id);
        if (abnormalOrder == null || abnormalOrder.getStatus() == 2 || abnormalOrder.getStatus() == 3) {
            return false;
        }

        // 更新处理状态
        abnormalOrder.setStatus(status);
        abnormalOrder.setProcessPlan(processPlan);
        abnormalOrder.setProcessorId(processorId);
        abnormalOrder.setProcessTime(LocalDateTime.now());
        abnormalOrder.setUpdateTime(LocalDateTime.now());

        return updateById(abnormalOrder);
    }

    @Override
    public AbnormalOrder getById(Long id) {
        return abnormalOrderMapper.selectById(id);
    }

    @Override
    public AbnormalOrder getByOrderTypeAndId(Integer orderType, Long orderId) {
        return abnormalOrderMapper.selectByOrderTypeAndId(orderType, orderId);
    }

    @Override
    public PageResult<AbnormalOrder> getPendingList(int page, int size) {
        Page<AbnormalOrder> pageInfo = new Page<>(page, size);
        QueryWrapper<AbnormalOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }

    @Override
    public PageResult<AbnormalOrder> getProcessingList(int page, int size) {
        Page<AbnormalOrder> pageInfo = new Page<>(page, size);
        QueryWrapper<AbnormalOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }

    @Override
    public PageResult<AbnormalOrder> getByAbnormalType(Integer abnormalType, int page, int size) {
        Page<AbnormalOrder> pageInfo = new Page<>(page, size);
        QueryWrapper<AbnormalOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("abnormal_type", abnormalType).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }
}
