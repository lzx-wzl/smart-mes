package com.lzx.smartmes.control.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mes_door")
public class Door {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long workshopId;

    private String doorCode;

    private String doorName;

    private String deviceIp;

    private String status;

    private Integer devicePort;
}