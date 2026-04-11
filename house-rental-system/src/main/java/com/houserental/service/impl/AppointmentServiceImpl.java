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
import com.houserental.common.result.PageResult;
import com.houserental.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 预约看房服务实现
 */
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
        appointment.setStatus(1); // 已确认
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
        Appointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus(3); // 已取消
        appointment.setRemark(reason);
        appointment.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(appointment) > 0;
    }

    @Override
    public boolean rejectAppointment(Long id, String reason) {
        Appointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus(4); // 已拒绝
        appointment.setRemark(reason);
        appointment.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(appointment) > 0;
    }

    @Override
    public PageResult<Appointment> pageAppointments(Map<String, Object> params) {
        Page<Appointment> page = new Page<>(Long.parseLong(params.get("page").toString()), Long.parseLong(params.get("size").toString()));
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();

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
        for (Appointment appointment : page.getRecords()) {
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

        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
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