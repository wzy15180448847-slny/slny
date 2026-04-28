package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.Bill;
import com.houserental.mapper.BillMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.BillService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Override
    public PageResult<Bill> pageBills(Map<String, Object> params) {
        long pageNum = params.containsKey("page") ? Long.parseLong(params.get("page").toString()) : 1;
        long pageSize = params.containsKey("size") ? Long.parseLong(params.get("size").toString()) : 10;
        Page<Bill> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Bill> wrapper = new QueryWrapper<>();

        if (params.containsKey("tenantId")) {
            wrapper.eq("tenant_id", params.get("tenantId"));
        }
        if (params.containsKey("landlordId")) {
            wrapper.eq("landlord_id", params.get("landlordId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq("status", params.get("status"));
        }
        if (params.containsKey("billType")) {
            wrapper.eq("bill_type", params.get("billType"));
        }

        baseMapper.selectPage(page, wrapper);
        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}