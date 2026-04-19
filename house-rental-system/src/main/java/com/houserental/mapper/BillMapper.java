package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.Bill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单Mapper
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {
}
