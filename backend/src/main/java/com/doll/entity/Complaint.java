package com.doll.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("complaints")
public class Complaint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long submitterId;
    private Long targetId;
    private Integer type;
    private String reason;
    private Integer status;
    private String result;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}
