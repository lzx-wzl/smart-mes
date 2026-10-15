package com.lzx.smartmes.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mes_process_route")
public class ProcessRoute {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long productId;

    private String routeCode;

    private String routeName;

    private String status;

    private Integer routeVersion;


}