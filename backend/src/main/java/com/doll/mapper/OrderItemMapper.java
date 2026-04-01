package com.doll.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doll.entity.OrderItem;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface OrderItemMapper extends BaseMapper<OrderItem> {

    @Select("SELECT product_id AS productId, COALESCE(SUM(quantity), 0) AS salesCount " +
            "FROM order_items GROUP BY product_id")
    List<Map<String, Object>> selectProductSalesSummary();
}
