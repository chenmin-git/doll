package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.AfterSale;
import com.doll.service.AfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/aftersale")
@CrossOrigin
public class AfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @PostMapping
    public Result<AfterSale> create(@RequestBody AfterSale afterSale) {
        afterSaleService.save(afterSale);
        return Result.success(afterSale);
    }

    @GetMapping("/buyer/{buyerId}")
    public Result<List<AfterSale>> getByBuyer(@PathVariable Long buyerId) {
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AfterSale::getBuyerId, buyerId);
        wrapper.orderByDesc(AfterSale::getCreateTime);
        return Result.success(afterSaleService.list(wrapper));
    }

    @PutMapping("/{id}")
    public Result<AfterSale> update(@PathVariable Long id, @RequestBody AfterSale afterSale) {
        afterSale.setId(id);
        afterSaleService.updateById(afterSale);
        return Result.success(afterSale);
    }

    @GetMapping("/list")
    public Result<List<AfterSale>> list() {
        return Result.success(afterSaleService.list());
    }
}
