package com.lzx.smartmes.process.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RouteCreateDTO {

    @NotBlank
    private String routeCode;

    @NotBlank
    private String routeName;

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer routeVersion;

    public Integer getVersion() {
        return routeVersion;
    }

    public void setVersion(Integer version) {
        routeVersion = version;
    }
}