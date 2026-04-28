package com.houserental.controller;

import com.houserental.common.result.Result;
import com.houserental.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/my")
    public Result<Object> getMyBills(@RequestParam Map<String, Object> params) {
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        params.put("tenantId", userId);
        return Result.success(billService.pageBills(params));
    }
}