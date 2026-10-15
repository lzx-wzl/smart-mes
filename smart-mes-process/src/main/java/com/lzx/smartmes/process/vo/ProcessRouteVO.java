package com.lzx.smartmes.process.vo;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ProcessRouteVO {

    private Long id;

    private Long productId;

    private String routeCode;

    private String routeName;

    private String status;

    private Integer version;
}