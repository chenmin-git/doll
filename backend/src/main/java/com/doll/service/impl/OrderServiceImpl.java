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
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("购买数量必须大于0");
        }

        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != null && product.getStatus() != 1) {
            throw new RuntimeException("商品已下架，无法购买");
        }
        if (product.getStock() == null || product.getStock() <= 0) {
            throw new RuntimeException("库存不足");
        }

        int affectedRows = productMapper.decreaseStockIfEnough(dto.getProductId(), dto.getQuantity());
        if (affectedRows <= 0) {
            throw new RuntimeException("库存不足");
        }

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
