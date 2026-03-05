package com.doll.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("after_sales")
public class AfterSale {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long buyerId;
    private Long sellerId;
    private String reason;
    private Integer status;
    private String description;
    private String images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
