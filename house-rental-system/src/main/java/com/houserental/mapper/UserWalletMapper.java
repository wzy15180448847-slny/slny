package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.UserWallet;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface UserWalletMapper extends BaseMapper<UserWallet> {

    UserWallet selectByUserId(@Param("userId") Long userId);

    int decreaseBalanceWithOptimisticLock(@Param("userId") Long userId, 
                                          @Param("amount") BigDecimal amount, 
                                          @Param("version") Integer version);

    int increaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    int decreaseBalanceWithCheck(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}