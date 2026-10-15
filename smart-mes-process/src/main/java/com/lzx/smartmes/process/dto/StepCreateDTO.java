package com.lzx.smartmes.process.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StepCreateDTO {

    @NotNull
    @Positive
    private Integer stepNo;

    @NotBlank
    private String processCode;

    @NotBlank
    private String processName;

    @NotNull
    private Long workstationId;

    @NotNull
    @Positive
    private BigDecimal standardDuration;

    @NotBlank
    private String parameterJson;
}