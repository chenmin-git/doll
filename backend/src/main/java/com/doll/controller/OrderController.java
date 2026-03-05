package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.dto.OrderDTO;
import com.doll.entity.Order;
import com.doll.service.OrderService;
import com.doll.service.OrderItemService;
import com.doll.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> create(@RequestBody OrderDTO dto) {
        Order order = orderService.createOrder(dto);
        return Result.success(order);
    }

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/buyer/{buyerId}")
    public Result<List<Order>> getByBuyer(@PathVariable Long buyerId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getBuyerId, buyerId);
        List<Order> orders = orderService.list(wrapper);
        for (Order order : orders) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            order.setItems(orderItemService.list(itemWrapper));
        }
        return Result.success(orders);
    }

    @GetMapping("/seller/{sellerId}")
    public Result<List<Order>> getBySeller(@PathVariable Long sellerId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getSellerId, sellerId);
        List<Order> orders = orderService.list(wrapper);
        for (Order order : orders) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            order.setItems(orderItemService.list(itemWrapper));
        }
        return Result.success(orders);
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            order.setItems(orderItemService.list(itemWrapper));
        }
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<List<Order>> list() {
        return Result.success(orderService.list());
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        orderService.updateById(order);
        return Result.success();
    }
}
