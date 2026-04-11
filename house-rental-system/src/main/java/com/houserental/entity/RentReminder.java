package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租金催缴实体类
 */
@Data
@TableName("biz_rent_reminder")
public class RentReminder extends BaseEntity {

    /**
     * 催缴编号
     */
    private String reminderNo;

    /**
     * 租约ID
     */
    private Long leaseAgreementId;

    /**
     * 租客ID
     */
    private Long tenantId;

    /**
     * 房东ID
     */
    private Long landlordId;

    /**
     * 催缴金额
     */
    private BigDecimal amount;

    /**
     * 应缴日期
     */
    private Date dueDate;

    /**
     * 催缴状态（0-待处理，1-已通知，2-已支付，3-已逾期）
     */
    private Integer status;

    /**
     * 催缴次数
     */
    private Integer reminderCount;

    /**
     * 最后催缴时间
     */
    private Date lastReminderTime;

    /**
     * 催缴备注
     */
    private String remark;

    /**
     * 租约信息
     */
    @TableField(exist = false)
    private LeaseAgreement leaseAgreement;

    /**
     * 租客信息
     */
    @TableField(exist = false)
    private User tenant;

    /**
     * 房东信息
     */
    @TableField(exist = false)
    private User landlord;
}
