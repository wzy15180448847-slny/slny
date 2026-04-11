package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.ElectronicSignature;
import com.houserental.mapper.ElectronicSignatureMapper;
import com.houserental.service.ElectronicSignatureService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 电子签章服务实现
 */
@Service
public class ElectronicSignatureServiceImpl extends ServiceImpl<ElectronicSignatureMapper, ElectronicSignature> implements ElectronicSignatureService {

    @Override
    public Long createSignature(ElectronicSignature signature) {
        // 生成签章编号
        signature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        // 设置创建时间
        signature.setCreateTime(LocalDateTime.now());
        signature.setUpdateTime(LocalDateTime.now());
        // 保存签章
        baseMapper.insert(signature);
        return signature.getId();
    }

    @Override
    public boolean completeSignature(Long id, String signatureData) {
        ElectronicSignature signature = baseMapper.selectById(id);
        if (signature == null) {
            return false;
        }
        signature.setSignatureData(signatureData);
        signature.setSigningTime(new java.util.Date());
        signature.setStatus(1); // 已签章
        signature.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(signature) > 0;
    }

    @Override
    public List<ElectronicSignature> getSignaturesByLeaseId(Long leaseId) {
        QueryWrapper<ElectronicSignature> wrapper = new QueryWrapper<>();
        wrapper.eq("lease_id", leaseId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public ElectronicSignature getSignatureByUserAndLease(Long userId, Long leaseId) {
        QueryWrapper<ElectronicSignature> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("lease_id", leaseId);
        return baseMapper.selectOne(wrapper);
    }
}