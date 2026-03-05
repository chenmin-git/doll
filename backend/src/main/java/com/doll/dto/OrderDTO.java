package com.doll.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDTO {
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String receiver;
    private String phone;
    private String address;
}
