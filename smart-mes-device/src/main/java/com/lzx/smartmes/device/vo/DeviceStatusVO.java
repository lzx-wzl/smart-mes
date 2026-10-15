package com.lzx.smartmes.device.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusVO {

    private Long deviceId;

    private String status;

    private BigDecimal temperature;

    private BigDecimal pressure;

    private BigDecimal speed;

    private LocalDateTime lastUpdateTime;
}