package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Auction;
import com.doll.entity.Order;
import com.doll.entity.OrderItem;
import com.doll.mapper.AuctionMapper;
import com.doll.service.OrderItemService;
import com.doll.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auction")
@CrossOrigin
public class AuctionController {

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/list")
    public Result<List<Auction>> list() {
        LambdaQueryWrapper<Auction> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Auction::getCreateTime);
        return Result.success(auctionMapper.selectList(wrapper));
    }

    @GetMapping("/seller/{sellerId}")
    public Result<List<Auction>> getBySeller(@PathVariable Long sellerId) {
        LambdaQueryWrapper<Auction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Auction::getSellerId, sellerId).orderByDesc(Auction::getCreateTime);
        return Result.success(auctionMapper.selectList(wrapper));
    }

    @PostMapping
    public Result<Auction> create(@RequestBody Auction auction) {
        if (auction.getCurrentPrice() == null) {
            auction.setCurrentPrice(auction.getStartPrice());
        }
        if (auction.getStatus() == null) {
            auction.setStatus(0);
        }
        auctionMapper.insert(auction);
        return Result.success(auction);
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Auction> update(@PathVariable Long id, @RequestBody Auction patch) {
        Auction auction = auctionMapper.selectById(id);
        if (auction == null) {
            return Result.error("拍卖不存在");
        }

        Integer newStatus = patch.getStatus();
        if (newStatus != null && newStatus == 2 && (auction.getStatus() == null || auction.getStatus() != 2)) {
            auction.setStatus(2);
            auctionMapper.updateById(auction);
            createWinningOrderIfNeeded(auction);
            return Result.success(auction);
        }

        if (patch.getProductId() != null) auction.setProductId(patch.getProductId());
        if (patch.getSellerId() != null) auction.setSellerId(patch.getSellerId());
        if (patch.getStartPrice() != null) auction.setStartPrice(patch.getStartPrice());
        if (patch.getCurrentPrice() != null) auction.setCurrentPrice(patch.getCurrentPrice());
        if (patch.getWinnerId() != null) auction.setWinnerId(patch.getWinnerId());
        if (patch.getStartTime() != null) auction.setStartTime(patch.getStartTime());
        if (patch.getEndTime() != null) auction.setEndTime(patch.getEndTime());
        if (patch.getStatus() != null) auction.setStatus(patch.getStatus());

        auctionMapper.updateById(auction);
        return Result.success(auction);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        auctionMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/bid")
    public Result<Void> bid(@PathVariable Long id, @RequestParam Long userId, @RequestParam BigDecimal bidPrice) {
        Auction auction = auctionMapper.selectById(id);
        if (auction == null) {
            return Result.error("拍卖不存在");
        }
        if (auction.getStatus() == null || auction.getStatus() != 1) {
            return Result.error("拍卖未在进行中");
        }

        BigDecimal currentPrice = auction.getCurrentPrice() == null ? auction.getStartPrice() : auction.getCurrentPrice();
        if (currentPrice == null || bidPrice.compareTo(currentPrice) <= 0) {
            return Result.error("出价必须高于当前价");
        }

        auction.setCurrentPrice(bidPrice);
        auction.setWinnerId(userId);
        auctionMapper.updateById(auction);
        return Result.success();
    }

    private void createWinningOrderIfNeeded(Auction auction) {
        if (auction.getWinnerId() == null) {
            return;
        }

        String auctionMarker = "AUCTION_WIN:" + auction.getId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getBuyerId, auction.getWinnerId())
                .eq(Order::getSellerId, auction.getSellerId())
                .eq(Order::getShippingInfo, auctionMarker);
        Order existing = orderService.getOne(wrapper, false);
        if (existing != null) {
            return;
        }

        BigDecimal dealPrice = auction.getCurrentPrice() != null
                ? auction.getCurrentPrice()
                : (auction.getStartPrice() != null ? auction.getStartPrice() : BigDecimal.ZERO);

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setBuyerId(auction.getWinnerId());
        order.setSellerId(auction.getSellerId());
        order.setTotalAmount(dealPrice);
        order.setStatus(0);
        order.setShippingInfo(auctionMarker);
        order.setIsDeleted(0);
        orderService.save(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(auction.getProductId());
        item.setQuantity(1);
        item.setPrice(dealPrice);
        orderItemService.save(item);
    }
}
