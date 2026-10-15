package com.lzx.smartmes.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("mes_process_step")
public class ProcessStep {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long routeId;

    private Long workstationId;

    private Integer stepNo;

    private String processCode;

    private String processName;

    private String parameterJson;

    private BigDecimal standardDuration;
}