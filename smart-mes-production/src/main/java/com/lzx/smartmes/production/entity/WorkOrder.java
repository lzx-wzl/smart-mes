package com.lzx.smartmes.production.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mes_work_order")
public class WorkOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String workOrderNo;

    private String status;

    private Long productionOrderId;

    private Long processRouteId;

    private Long workshopId;

    private BigDecimal planQuantity;

    private BigDecimal completedQuantity;

    private Integer currentStepNo;

    private LocalDateTime plannedStartTime;

    private LocalDateTime plannedEndTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;
}