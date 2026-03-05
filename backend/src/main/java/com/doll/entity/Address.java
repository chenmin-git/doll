package com.doll.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("addresses")
public class Address {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
    
    // 临时字段，用于前端展示完整地址
    @TableField(exist = false)
    private String fullAddress;
    
    public String getFullAddress() {
        return (province != null ? province : "") + 
               (city != null ? city : "") + 
               (district != null ? district : "") + 
               (detail != null ? detail : "");
    }
}
