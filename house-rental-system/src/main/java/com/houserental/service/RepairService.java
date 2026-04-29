package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.Repair;
import com.houserental.common.result.PageResult;

import java.util.Map;

public interface RepairService extends IService<Repair> {

    Long createRepair(Repair repair);

    boolean cancelRepair(Long id);

    boolean evaluateRepair(Long id, String content, Integer score);

    PageResult<Repair> pageRepairs(Map<String, Object> params);

    boolean acceptRepair(Long id);

    boolean completeRepair(Long id);
}