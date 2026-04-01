package com.doll.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doll.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE products SET stock = stock - #{quantity}, update_time = NOW() " +
            "WHERE id = #{productId} AND is_deleted = 0 AND stock >= #{quantity}")
    int decreaseStockIfEnough(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
