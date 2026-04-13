package com.houserental.mapper;

import com.houserental.entity.PaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    List<PaymentRecord> selectByUserId(Long userId);

    PaymentRecord selectByOrderNo(String orderNo);

    /**
     * 按月统计支付成功的总金额
     */
    java.math.BigDecimal sumPaymentByMonth(@Param("month") String month);
}
