package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.Appointment;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.mapper.AppointmentMapper;
import com.houserental.mapper.HouseMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.exception.BusinessException;
import com.houserental.common.result.PageResult;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 预约看房服务实现
 */
@Slf4j
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long createAppointment(Appointment appointment) {
        // 生成预约编号
        appointment.setAppointmentNo("APT" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        // 设置创建时间
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        // 保存预约
        baseMapper.insert(appointment);
        return appointment.getId();
    }

    @Override
    public boolean confirmAppointment(Long id) {
        Appointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(appointment.getLandlordId())) {
            throw new BusinessException("无权操作他人房源的预约");
        }
        
        appointment.setStatus(1);
        appointment.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(appointment) > 0;
    }

    @Override
    public boolean completeAppointment(Long id) {
        Appointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus(2); // 已完成
        appointment.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(appointment) > 0;
    }

    @Override
    public boolean cancelAppointment(Long id, String reason) {
        log.info("取消预约，ID: {}, 原因: {}", id, reason);
        
        Appointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            log.warn("预约不存在，ID: {}", id);
            return false;
        }
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("当前用户ID: {}, 预约租户ID: {}, 预约房东ID: {}", currentUserId, appointment.getTenantId(), appointment.getLandlordId());
        
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }
        
        if (!currentUserId.equals(appointment.getTenantId()) && !currentUserId.equals(appointment.getLandlordId())) {
            throw new BusinessException("无权取消他人的预约");
        }
        
        appointment.setStatus(3);
        appointment.setRemark(reason);
        appointment.setUpdateTime(LocalDateTime.now());
        boolean success = baseMapper.updateById(appointment) > 0;
        log.info("取消预约结果: {}", success);
        return success;
    }

    @Override
    public boolean rejectAppointment(Long id, String reason) {
        Appointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(appointment.getLandlordId())) {
            throw new BusinessException("无权操作他人房源的预约");
        }
        
        appointment.setStatus(4);
        appointment.setRemark(reason);
        appointment.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(appointment) > 0;
    }

    @Override
    public PageResult<Appointment> pageAppointments(Map<String, Object> params) {
        long pageNum = params.containsKey("page") ? Long.parseLong(params.get("page").toString()) : 1;
        long pageSize = params.containsKey("size") ? Long.parseLong(params.get("size").toString()) : 10;
        Page<Appointment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();

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

        baseMapper.selectPage(page, wrapper);

        List<Map<String, Object>> resultList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Appointment appointment : page.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", appointment.getId());
            
            if (appointment.getHouseId() != null) {
                House house = houseMapper.selectById(appointment.getHouseId());
                if (house != null) {
                    item.put("houseName", house.getTitle());
                    item.put("address", house.getAddress() != null ? house.getAddress() : "");
                }
            }
            
            if (appointment.getLandlordId() != null) {
                User landlord = userMapper.selectById(appointment.getLandlordId());
                if (landlord != null) {
                    item.put("landlordName", landlord.getUsername());
                    item.put("landlordPhone", landlord.getPhone());
                }
            }
            
            if (appointment.getAppointmentTime() != null) {
                item.put("date", formatter.format(appointment.getAppointmentTime()));
            }
            
            item.put("status", convertStatus(appointment.getStatus()));
            
            resultList.add(item);
        }

        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), (List) resultList);
    }
    
    private String convertStatus(Integer status) {
        if (status == null) {
            return "PENDING";
        }
        switch (status) {
            case 0: return "PENDING";
            case 1: return "CONFIRMED";
            case 2: return "COMPLETED";
            case 3: return "CANCELLED";
            case 4: return "REJECTED";
            default: return "PENDING";
        }
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        Appointment appointment = baseMapper.selectById(id);
        if (appointment != null) {
            // 填充关联数据
            if (appointment.getHouseId() != null) {
                House house = houseMapper.selectById(appointment.getHouseId());
                appointment.setHouse(house);
            }
            if (appointment.getTenantId() != null) {
                User tenant = userMapper.selectById(appointment.getTenantId());
                appointment.setTenant(tenant);
            }
            if (appointment.getLandlordId() != null) {
                User landlord = userMapper.selectById(appointment.getLandlordId());
                appointment.setLandlord(landlord);
            }
        }
        return appointment;
    }
}