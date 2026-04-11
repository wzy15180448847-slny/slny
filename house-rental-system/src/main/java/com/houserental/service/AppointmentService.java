package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.Appointment;
import com.houserental.common.result.PageResult;

import java.util.Map;

/**
 * 预约看房服务
 */
public interface AppointmentService extends IService<Appointment> {

    /**
     * 创建预约
     * @param appointment 预约信息
     * @return 预约ID
     */
    Long createAppointment(Appointment appointment);

    /**
     * 确认预约
     * @param id 预约ID
     * @return 是否成功
     */
    boolean confirmAppointment(Long id);

    /**
     * 完成预约
     * @param id 预约ID
     * @return 是否成功
     */
    boolean completeAppointment(Long id);

    /**
     * 取消预约
     * @param id 预约ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelAppointment(Long id, String reason);

    /**
     * 拒绝预约
     * @param id 预约ID
     * @param reason 拒绝原因
     * @return 是否成功
     */
    boolean rejectAppointment(Long id, String reason);

    /**
     * 分页查询预约
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<Appointment> pageAppointments(Map<String, Object> params);

    /**
     * 根据ID查询预约详情
     * @param id 预约ID
     * @return 预约信息
     */
    Appointment getAppointmentById(Long id);
}