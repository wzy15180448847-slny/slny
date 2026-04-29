package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.Repair;
import com.houserental.mapper.RepairMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.RepairService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    @Override
    public Long createRepair(Repair repair) {
        repair.setStatus(0);
        repair.setCreateTime(LocalDateTime.now());
        repair.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(repair);
        return repair.getId();
    }

    @Override
    public boolean cancelRepair(Long id) {
        Repair repair = baseMapper.selectById(id);
        if (repair == null) {
            return false;
        }
        repair.setStatus(3);
        repair.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(repair) > 0;
    }

    @Override
    public boolean evaluateRepair(Long id, String content, Integer score) {
        Repair repair = baseMapper.selectById(id);
        if (repair == null) {
            return false;
        }
        repair.setEvaluateContent(content);
        repair.setEvaluateScore(score);
        repair.setStatus(2);
        repair.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(repair) > 0;
    }

    @Override
    public PageResult<Repair> pageRepairs(Map<String, Object> params) {
        long pageNum = params.containsKey("page") ? Long.parseLong(params.get("page").toString()) : 1;
        long pageSize = params.containsKey("size") ? Long.parseLong(params.get("size").toString()) : 10;
        Page<Repair> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Repair> wrapper = new QueryWrapper<>();

        if (params.containsKey("tenantId")) {
            wrapper.eq("tenant_id", params.get("tenantId"));
        }
        if (params.containsKey("landlordId")) {
            wrapper.eq("landlord_id", params.get("landlordId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq("status", params.get("status"));
        }

        baseMapper.selectPage(page, wrapper);
        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    public boolean acceptRepair(Long id) {
        Repair repair = baseMapper.selectById(id);
        if (repair == null) {
            return false;
        }
        repair.setStatus(1);
        repair.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(repair) > 0;
    }

    @Override
    public boolean completeRepair(Long id) {
        Repair repair = baseMapper.selectById(id);
        if (repair == null) {
            return false;
        }
        repair.setStatus(2);
        repair.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(repair) > 0;
    }
}