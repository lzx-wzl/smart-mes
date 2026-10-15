package com.lzx.smartmes.production.vo;

import com.lzx.smartmes.common.enums.ProductionOrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductionOrderVO {

    private Long id;

    private String orderNo;

    private String erpOrderNo;

    private Long productId;

    private BigDecimal planQuantity;

    private BigDecimal completedQuantity;

    private ProductionOrderStatus status;

    private Integer version;

    private LocalDateTime plannedStartTime;

    private LocalDateTime plannedEndTime;
}