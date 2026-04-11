package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.LeaseAgreement;
import com.houserental.entity.RentReminder;
import com.houserental.mapper.LeaseAgreementMapper;
import com.houserental.mapper.RentReminderMapper;
import com.houserental.service.RentReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 租金催缴服务实现类
 */
@Service
public class RentReminderServiceImpl extends ServiceImpl<RentReminderMapper, RentReminder> implements RentReminderService {

    @Autowired
    private RentReminderMapper rentReminderMapper;

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Override
    @Transactional
    public RentReminder createRentReminder(Long leaseAgreementId, BigDecimal amount, Date dueDate) {
        // 查询租约信息
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(leaseAgreementId);
        if (leaseAgreement == null) {
            throw new RuntimeException("租约不存在");
        }

        // 创建催缴记录
        RentReminder rentReminder = new RentReminder();
        rentReminder.setReminderNo(generateReminderNo());
        rentReminder.setLeaseAgreementId(leaseAgreementId);
        rentReminder.setTenantId(leaseAgreement.getTenantId());
        rentReminder.setLandlordId(leaseAgreement.getLandlordId());
        rentReminder.setAmount(amount);
        rentReminder.setDueDate(dueDate);
        rentReminder.setStatus(0); // 待处理
        rentReminder.setReminderCount(0);
        rentReminder.setRemark("租金催缴 - 租约" + leaseAgreement.getLeaseNo());

        rentReminderMapper.insert(rentReminder);
        return rentReminder;
    }

    @Override
    @Transactional
    public boolean sendReminderNotification(Long reminderId) {
        // 查询催缴记录
        RentReminder rentReminder = rentReminderMapper.selectById(reminderId);
        if (rentReminder == null) {
            return false;
        }

        // 模拟发送通知（实际项目中可以集成短信、邮件等通知方式）
        System.out.println("发送租金催缴通知给租客：" + rentReminder.getTenantId());
        System.out.println("催缴金额：" + rentReminder.getAmount());
        System.out.println("应缴日期：" + rentReminder.getDueDate());

        // 更新催缴状态和次数
        rentReminder.setStatus(1); // 已通知
        rentReminder.setReminderCount(rentReminder.getReminderCount() + 1);
        rentReminder.setLastReminderTime(new Date());
        rentReminderMapper.updateById(rentReminder);

        return true;
    }

    @Override
    @Transactional
    public boolean updateReminderStatus(Long reminderId, Integer status) {
        RentReminder rentReminder = rentReminderMapper.selectById(reminderId);
        if (rentReminder == null) {
            return false;
        }

        rentReminder.setStatus(status);
        rentReminderMapper.updateById(rentReminder);
        return true;
    }

    @Override
    public List<RentReminder> getPendingReminders() {
        return rentReminderMapper.selectList(new QueryWrapper<RentReminder>().eq("status", 0));
    }

    @Override
    public List<RentReminder> getOverdueReminders() {
        return rentReminderMapper.selectList(new QueryWrapper<RentReminder>()
                .lt("due_date", new Date())
                .ne("status", 2)); // 排除已支付的
    }

    @Override
    @Transactional
    public int autoGenerateReminders() {
        // 查询所有已生效且未到期的租约
        List<LeaseAgreement> leaseAgreements = leaseAgreementMapper.selectList(new QueryWrapper<LeaseAgreement>()
                .eq("status", 2) // 已生效
                .gt("end_date", new Date()));

        int count = 0;
        for (LeaseAgreement leaseAgreement : leaseAgreements) {
            // 计算下一个缴费日期（这里简化处理，实际应该根据付款方式和起租日期计算）
            Date nextDueDate = calculateNextDueDate(leaseAgreement);
            if (nextDueDate != null) {
                // 检查是否已经存在相同日期的催缴单
                RentReminder existingReminder = rentReminderMapper.selectOne(new QueryWrapper<RentReminder>()
                        .eq("lease_agreement_id", leaseAgreement.getId())
                        .eq("due_date", nextDueDate));
                
                if (existingReminder == null) {
                    // 创建催缴单
                    createRentReminder(leaseAgreement.getId(), leaseAgreement.getRentPrice(), nextDueDate);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 生成催缴编号
     */
    private String generateReminderNo() {
        return "REM" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 计算下一个缴费日期
     */
    private Date calculateNextDueDate(LeaseAgreement leaseAgreement) {
        // 这里简化处理，实际应该根据付款方式和起租日期计算
        // 例如：月付则每月同一天，季付则每三个月同一天，以此类推
        Date now = new Date();
        // 假设下个月的今天为缴费日期
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(java.util.Calendar.MONTH, 1);
        return calendar.getTime();
    }
}
