package com.lzx.smartmes.integration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ErpOrderDTO {

    @NotBlank
    private String erpOrderNo;

    @NotBlank
    private String productCode;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    private LocalDateTime plannedStartTime;

    @NotNull
    private LocalDateTime plannedEndTime;
}