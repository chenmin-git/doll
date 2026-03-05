package com.doll.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doll.dto.OrderDTO;
import com.doll.entity.Order;

public interface OrderService extends IService<Order> {
    Order createOrder(OrderDTO orderDTO);
}
