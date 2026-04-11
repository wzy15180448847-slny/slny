package com.houserental.mapper;

import com.houserental.entity.PaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    List<PaymentRecord> selectByUserId(Long userId);

    PaymentRecord selectByOrderNo(String orderNo);
}
