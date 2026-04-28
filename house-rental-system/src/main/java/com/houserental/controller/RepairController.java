package com.houserental.controller;

import com.houserental.entity.Repair;
import com.houserental.common.result.Result;
import com.houserental.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/repairs")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @GetMapping("/my")
    public Result<Object> getMyRepairs(@RequestParam Map<String, Object> params) {
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        params.put("tenantId", userId);
        return Result.success(repairService.pageRepairs(params));
    }

    @PostMapping
    public Result<Long> createRepair(@RequestBody Repair repair) {
        Long id = repairService.createRepair(repair);
        return Result.success(id);
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelRepair(@PathVariable Long id) {
        boolean success = repairService.cancelRepair(id);
        return success ? Result.success() : Result.error("取消报修失败");
    }

    @PutMapping("/{id}/evaluate")
    public Result<Void> evaluateRepair(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        String content = (String) data.get("evaluateContent");
        Integer score = (Integer) data.get("evaluateScore");
        boolean success = repairService.evaluateRepair(id, content, score);
        return success ? Result.success() : Result.error("评价失败");
    }
}