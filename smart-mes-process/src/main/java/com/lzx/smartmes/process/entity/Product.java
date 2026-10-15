package com.lzx.smartmes.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mes_product")
public class Product {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String productCode;

    private String productName;

    private String specification;

    private String status;
}