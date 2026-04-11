package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.PenaltyRule;
import com.houserental.mapper.PenaltyRuleMapper;
import com.houserental.service.PenaltyRuleService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 违约金规则服务实现
 */
@Service
public class PenaltyRuleServiceImpl extends ServiceImpl<PenaltyRuleMapper, PenaltyRule> implements PenaltyRuleService {

    @Override
    public PenaltyRule getEnabledRuleByType(String ruleType) {
        QueryWrapper<PenaltyRule> wrapper = new QueryWrapper<>();
        wrapper.eq("rule_type", ruleType);
        wrapper.eq("status", 1); // 启用状态
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public BigDecimal calculatePenalty(String ruleType, Map<String, Object> params) {
        PenaltyRule rule = getEnabledRuleByType(ruleType);
        if (rule == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal penalty = BigDecimal.ZERO;

        // 根据计算方式计算违约金
        switch (rule.getCalculationMethod()) {
            case "RENT_MULTIPLE":
                // 按租金倍数计算
                if (params.containsKey("rentPrice")) {
                    BigDecimal rentPrice = (BigDecimal) params.get("rentPrice");
                    penalty = rentPrice;
                }
                break;
            case "DAILY_PERCENTAGE":
                // 按日百分比计算
                if (params.containsKey("baseAmount") && params.containsKey("days")) {
                    BigDecimal baseAmount = (BigDecimal) params.get("baseAmount");
                    int days = (int) params.get("days");
                    penalty = baseAmount.multiply(rule.getPercentage()).multiply(new BigDecimal(days));
                }
                break;
            case "ACTUAL_COST":
                // 按实际损失计算
                if (params.containsKey("actualCost")) {
                    penalty = (BigDecimal) params.get("actualCost");
                }
                break;
            default:
                break;
        }

        // 应用最低和最高限制
        if (rule.getMinAmount() != null && penalty.compareTo(rule.getMinAmount()) < 0) {
            penalty = rule.getMinAmount();
        }
        if (rule.getMaxAmount() != null && penalty.compareTo(rule.getMaxAmount()) > 0) {
            penalty = rule.getMaxAmount();
        }

        return penalty;
    }
}