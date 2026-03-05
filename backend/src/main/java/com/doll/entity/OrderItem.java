package com.doll.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_items")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 临时字段，用于前端展示
    @TableField(exist = false)
    private String productName;
    
    @TableField(exist = false)
    private String productImage;
}
