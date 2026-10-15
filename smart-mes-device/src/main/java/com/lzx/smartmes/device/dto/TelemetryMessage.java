package com.lzx.smartmes.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TelemetryMessage {

    @NotBlank
    private String messageId;

    @NotBlank
    private String deviceCode;

    @NotNull
    private BigDecimal temperature;

    @NotNull
    private BigDecimal pressure;

    @NotNull
    private BigDecimal speed;

    @NotBlank
    private String status;

    private String alarmCode;

    @NotNull
    private LocalDateTime timestamp;
}