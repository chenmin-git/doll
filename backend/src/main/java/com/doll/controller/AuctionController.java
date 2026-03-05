package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Auction;
import com.doll.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auction")
@CrossOrigin
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping
    public Result<Auction> create(@RequestBody Auction auction) {
        auctionService.save(auction);
        return Result.success(auction);
    }

    @GetMapping("/seller/{sellerId}")
    public Result<List<Auction>> getBySeller(@PathVariable Long sellerId) {
        LambdaQueryWrapper<Auction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Auction::getSellerId, sellerId);
        wrapper.orderByDesc(Auction::getCreateTime);
        return Result.success(auctionService.list(wrapper));
    }

    @PutMapping("/{id}")
    public Result<Auction> update(@PathVariable Long id, @RequestBody Auction auction) {
        auction.setId(id);
        auctionService.updateById(auction);
        return Result.success(auction);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        auctionService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Auction> getById(@PathVariable Long id) {
        return Result.success(auctionService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<Auction>> list() {
        return Result.success(auctionService.list());
    }
}
