package com.lzx.smartmes.integration.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mes_product")
public class ProductRef {

    @TableId
    private Long id;

    private String productCode;

    private String productName;
}