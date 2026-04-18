package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 预约看房实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_appointment")
public class Appointment extends BaseEntity {

    /**
     * 预约编号
     */
    private String appointmentNo;

    /**
     * 房源ID
     */
    private Long houseId;

    /**
     * 租客ID
     */
    private Long tenantId;

    /**
     * 房东ID
     */
    private Long landlordId;

    /**
     * 预约时间
     */
    private Date appointmentTime;

    /**
     * 状态（0-待确认，1-已确认，2-已完成，3-已取消，4-已拒绝）
     */
    private Integer status;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 房源信息
     */
    @TableField(exist = false)
    private House house;

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