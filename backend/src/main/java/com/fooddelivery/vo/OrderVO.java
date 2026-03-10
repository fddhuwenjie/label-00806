package com.fooddelivery.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fooddelivery.entity.OrderItem;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long merchantId;
    private String merchantName;
    private String merchantLogo;
    private String addressSnapshot;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal actualAmount;
    private Integer status;
    private String statusText;
    private String remark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliverTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;
    
    private List<OrderItem> items;
}
