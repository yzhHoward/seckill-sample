package org.example.controller;

import com.alibaba.fastjson.JSONObject;
import org.example.dto.Result;
import org.example.entity.Commodity;
import org.example.service.SecKillService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class SecKillController {

    private final SecKillService secKillService;

    public SecKillController(SecKillService secKillService) {
        this.secKillService = secKillService;
    }

    @GetMapping("/list")
    public Result getAllCommodity () {
        List<Commodity> list = secKillService.findAll();
        return Result.ok().data("commodityList", list);
    }

    @GetMapping("/findById")
    public Result findById(@RequestParam("id") Long id) {
        Commodity commodity = secKillService.findById(id);
        return Result.ok().data("commodity", commodity);
    }

    @PostMapping("/buy")
    public Result buy(@RequestBody JSONObject request) {
        Long commodityId = request.getLong("commodityId");
        BigDecimal money = request.getBigDecimal("money");
        String phone = request.getString("phone");
        if (secKillService.buy(commodityId, money, phone)) {
            return Result.ok();
        } else {
            return Result.error();
        }
    }
}
