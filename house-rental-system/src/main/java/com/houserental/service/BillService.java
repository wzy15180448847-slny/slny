package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.Bill;
import com.houserental.common.result.PageResult;

import java.util.Map;

public interface BillService extends IService<Bill> {

    PageResult<Bill> pageBills(Map<String, Object> params);
}