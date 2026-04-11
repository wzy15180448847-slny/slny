package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.ElectronicSignature;
import com.houserental.entity.House;
import com.houserental.entity.LeaseAgreement;
import com.houserental.entity.User;
import com.houserental.mapper.ElectronicSignatureMapper;
import com.houserental.mapper.HouseMapper;
import com.houserental.mapper.LeaseAgreementMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.LeaseAgreementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 租约服务实现
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement> implements LeaseAgreementService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ElectronicSignatureMapper electronicSignatureMapper;

    @Override
    @Transactional
    public Long createLease(LeaseAgreement lease) {
        // 生成租约编号
        lease.setLeaseNo("LEASE" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        // 设置创建时间
        lease.setCreateTime(LocalDateTime.now());
        lease.setUpdateTime(LocalDateTime.now());
        // 保存租约
        baseMapper.insert(lease);

        // 创建电子签章记录
        createSignatureRecords(lease);

        return lease.getId();
    }

    private void createSignatureRecords(LeaseAgreement lease) {
        // 创建租客签章记录
        ElectronicSignature tenantSignature = new ElectronicSignature();
        tenantSignature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        tenantSignature.setLeaseId(lease.getId());
        tenantSignature.setUserId(lease.getTenantId());
        tenantSignature.setUserType("TENANT");
        tenantSignature.setStatus(0); // 待签章
        tenantSignature.setCreateTime(LocalDateTime.now());
        tenantSignature.setUpdateTime(LocalDateTime.now());
        electronicSignatureMapper.insert(tenantSignature);

        // 创建房东签章记录
        ElectronicSignature landlordSignature = new ElectronicSignature();
        landlordSignature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        landlordSignature.setLeaseId(lease.getId());
        landlordSignature.setUserId(lease.getLandlordId());
        landlordSignature.setUserType("LANDLORD");
        landlordSignature.setStatus(0); // 待签章
        landlordSignature.setCreateTime(LocalDateTime.now());
        landlordSignature.setUpdateTime(LocalDateTime.now());
        electronicSignatureMapper.insert(landlordSignature);
    }

    @Override
    @Transactional
    public boolean signLease(Long id, Long userId, String userType, String signatureData) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
        }

        // 更新电子签章
        QueryWrapper<ElectronicSignature> wrapper = new QueryWrapper<>();
        wrapper.eq("lease_id", id);
        wrapper.eq("user_id", userId);
        wrapper.eq("user_type", userType);
        ElectronicSignature signature = electronicSignatureMapper.selectOne(wrapper);
        if (signature != null) {
            signature.setSignatureData(signatureData);
            signature.setSigningTime(new java.util.Date());
            signature.setStatus(1); // 已签章
            signature.setUpdateTime(LocalDateTime.now());
            electronicSignatureMapper.updateById(signature);
        }

        // 检查是否所有签章都已完成
        QueryWrapper<ElectronicSignature> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("lease_id", id);
        checkWrapper.eq("status", 0); // 待签章
        long count = electronicSignatureMapper.selectCount(checkWrapper);
        if (count == 0) {
            // 所有签章完成，更新租约状态
            lease.setStatus(1); // 已签署
            lease.setSigningDate(new java.util.Date());
            lease.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(lease);
        }

        return true;
    }

    @Override
    public boolean effectiveLease(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
        }
        lease.setStatus(2); // 已生效
        lease.setEffectiveDate(new java.util.Date());
        lease.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(lease) > 0;
    }

    @Override
    public boolean terminateLease(Long id, String reason) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
        }
        lease.setStatus(4); // 已解约
        lease.setTerminationDate(new java.util.Date());
        lease.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(lease) > 0;
    }

    @Override
    public PageResult<LeaseAgreement> pageLeases(Map<String, Object> params) {
        Page<LeaseAgreement> page = new Page<>(Long.parseLong(params.get("page").toString()), Long.parseLong(params.get("size").toString()));
        QueryWrapper<LeaseAgreement> wrapper = new QueryWrapper<>();

        // 添加查询条件
        if (params.containsKey("houseId")) {
            wrapper.eq("house_id", params.get("houseId"));
        }
        if (params.containsKey("tenantId")) {
            wrapper.eq("tenant_id", params.get("tenantId"));
        }
        if (params.containsKey("landlordId")) {
            wrapper.eq("landlord_id", params.get("landlordId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq("status", params.get("status"));
        }

        // 分页查询
        baseMapper.selectPage(page, wrapper);

        // 填充关联数据
        for (LeaseAgreement lease : page.getRecords()) {
            fillLeaseDetails(lease);
        }

        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    public LeaseAgreement getLeaseById(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease != null) {
            fillLeaseDetails(lease);
        }
        return lease;
    }

    private void fillLeaseDetails(LeaseAgreement lease) {
        // 填充房源信息
        if (lease.getHouseId() != null) {
            House house = houseMapper.selectById(lease.getHouseId());
            lease.setHouse(house);
        }
        // 填充租客信息
        if (lease.getTenantId() != null) {
            User tenant = userMapper.selectById(lease.getTenantId());
            lease.setTenant(tenant);
        }
        // 填充房东信息
        if (lease.getLandlordId() != null) {
            User landlord = userMapper.selectById(lease.getLandlordId());
            lease.setLandlord(landlord);
        }
        // 填充电子签章信息
        QueryWrapper<ElectronicSignature> wrapper = new QueryWrapper<>();
        wrapper.eq("lease_id", lease.getId());
        List<ElectronicSignature> signatures = electronicSignatureMapper.selectList(wrapper);
        lease.setSignatures(signatures);
    }
}