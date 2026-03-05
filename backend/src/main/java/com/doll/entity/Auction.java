package com.doll.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("auctions")
public class Auction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long sellerId;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private Long winnerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}
