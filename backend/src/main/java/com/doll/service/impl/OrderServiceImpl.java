package com.doll.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.dto.OrderDTO;
import com.doll.entity.Order;
import com.doll.entity.OrderItem;
import com.doll.entity.Product;
import com.doll.mapper.OrderMapper;
import com.doll.mapper.ProductMapper;
import com.doll.service.OrderItemService;
import com.doll.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("商品不存在或库存不足");
        }
        if (product.getStatus() != null && product.getStatus() != 1) {
            throw new RuntimeException("商品已下架，无法购买");
        }

        product.setStock(product.getStock() - dto.getQuantity());
        productMapper.updateById(product);

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setBuyerId(dto.getBuyerId());
        order.setSellerId(dto.getSellerId());
        order.setTotalAmount(dto.getTotalAmount());

        String shippingInfo = dto.getReceiver() + "," + dto.getPhone() + "," + dto.getAddress();
        order.setShippingInfo(shippingInfo);

        order.setStatus(0);
        order.setIsDeleted(0);
        this.save(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(dto.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(product.getPrice());
        orderItemService.save(item);

        return order;
    }
}
