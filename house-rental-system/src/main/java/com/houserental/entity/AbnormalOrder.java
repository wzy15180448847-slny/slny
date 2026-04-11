package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 异常订单实体
 */
@TableName("biz_abnormal_order")
public class AbnormalOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单类型（1-预约订单，2-租赁合同）
     */
    @TableField(value = "order_type", exist = true)
    private Integer orderType;

    /**
     * 订单ID
     */
    @TableField(value = "order_id", exist = true)
    private Long orderId;

    /**
     * 异常类型（1-支付异常，2-预约取消，3-合同违约，4-其他）
     */
    @TableField(value = "abnormal_type", exist = true)
    private Integer abnormalType;

    /**
     * 异常描述
     */
    @TableField(value = "description", exist = true)
    private String description;

    /**
     * 处理状态（0-待处理，1-处理中，2-已处理，3-已关闭）
     */
    @TableField(value = "status", exist = true)
    private Integer status = 0;

    /**
     * 处理方案
     */
    @TableField(value = "process_plan", exist = true)
    private String processPlan;

    /**
     * 处理人ID
     */
    @TableField(value = "processor_id", exist = true)
    private Long processorId;

    /**
     * 处理时间
     */
    @TableField(value = "process_time", exist = true)
    private LocalDateTime processTime;

    // Getters and Setters
    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getAbnormalType() {
        return abnormalType;
    }

    public void setAbnormalType(Integer abnormalType) {
        this.abnormalType = abnormalType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProcessPlan() {
        return processPlan;
    }

    public void setProcessPlan(String processPlan) {
        this.processPlan = processPlan;
    }

    public Long getProcessorId() {
        return processorId;
    }

    public void setProcessorId(Long processorId) {
        this.processorId = processorId;
    }

    public LocalDateTime getProcessTime() {
        return processTime;
    }

    public void setProcessTime(LocalDateTime processTime) {
        this.processTime = processTime;
    }
}