package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.PenaltyRule;

/**
 * 违约金规则服务
 */
public interface PenaltyRuleService extends IService<PenaltyRule> {

    /**
     * 根据规则类型获取启用的规则
     * @param ruleType 规则类型
     * @return 规则信息
     */
    PenaltyRule getEnabledRuleByType(String ruleType);

    /**
     * 计算违约金
     * @param ruleType 规则类型
     * @param params 计算参数
     * @return 违约金金额
     */
    java.math.BigDecimal calculatePenalty(String ruleType, java.util.Map<String, Object> params);
}