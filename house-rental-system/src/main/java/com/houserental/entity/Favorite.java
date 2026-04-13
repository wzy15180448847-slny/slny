package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("biz_favorite")
public class Favorite extends BaseEntity {

    private Long userId;

    private Long houseId;

    private LocalDateTime createdTime;
}