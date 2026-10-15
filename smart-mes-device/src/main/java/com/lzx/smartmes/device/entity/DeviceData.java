package com.lzx.smartmes.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mes_device_data")
public class DeviceData {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long deviceId;

    private BigDecimal temperature;

    private BigDecimal pressure;

    private BigDecimal speed;

    private String runningStatus;

    private String alarmCode;

    private LocalDateTime collectTime;
}