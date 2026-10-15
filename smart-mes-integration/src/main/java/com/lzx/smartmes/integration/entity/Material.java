package com.lzx.smartmes.integration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mes_material")
public class Material {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String materialCode;

    private String materialName;

    private String specification;

    private String unit;
}