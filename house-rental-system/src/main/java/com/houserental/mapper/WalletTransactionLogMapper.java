package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.WalletTransactionLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionLogMapper extends BaseMapper<WalletTransactionLog> {

    List<WalletTransactionLog> selectByUserId(@Param("userId") Long userId);

    List<WalletTransactionLog> selectByOrderNo(@Param("orderNo") String orderNo);
}