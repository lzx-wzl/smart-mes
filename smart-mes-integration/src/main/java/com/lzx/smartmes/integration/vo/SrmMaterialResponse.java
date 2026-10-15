package com.lzx.smartmes.integration.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrmMaterialResponse {

    private String materialCode;

    private String supplierCode;

    private BigDecimal availableStock;
}