package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报修实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_repair")
public class Repair extends BaseEntity {

    private Long houseId;

    private Long tenantId;

    private Long landlordId;

    private String repairType;

    private String description;

    private Integer status;

    private String evaluateContent;

    private Integer evaluateScore;
}