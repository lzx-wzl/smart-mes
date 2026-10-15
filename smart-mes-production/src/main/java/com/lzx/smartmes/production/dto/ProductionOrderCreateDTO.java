package com.lzx.smartmes.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductionOrderCreateDTO {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private BigDecimal planQuantity;

    @NotBlank
    private String priority;

    @NotNull
    private LocalDateTime plannedStartTime;

    @NotNull
    private LocalDateTime plannedEndTime;
}