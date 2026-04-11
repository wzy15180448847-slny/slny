package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.RentReminder;
import java.util.List;

/**
 * 租金催缴服务接口
 */
public interface RentReminderService extends IService<RentReminder> {

    /**
     * 生成租金催缴单
     * @param leaseAgreementId 租约ID
     * @param amount 催缴金额
     * @param dueDate 应缴日期
     * @return 催缴记录
     */
    RentReminder createRentReminder(Long leaseAgreementId, java.math.BigDecimal amount, java.util.Date dueDate);

    /**
     * 发送催缴通知
     * @param reminderId 催缴记录ID
     * @return 发送结果
     */
    boolean sendReminderNotification(Long reminderId);

    /**
     * 更新催缴状态
     * @param reminderId 催缴记录ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateReminderStatus(Long reminderId, Integer status);

    /**
     * 查询待处理的催缴单
     * @return 催缴单列表
     */
    List<RentReminder> getPendingReminders();

    /**
     * 查询逾期未支付的催缴单
     * @return 催缴单列表
     */
    List<RentReminder> getOverdueReminders();

    /**
     * 自动生成租金催缴单
     * @return 生成的催缴单数量
     */
    int autoGenerateReminders();
}
