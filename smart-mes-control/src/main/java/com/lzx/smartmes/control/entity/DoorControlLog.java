package com.lzx.smartmes.control.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mes_door_control_log")
public class DoorControlLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long doorId;

    private String action;

    private String result;

    private String reason;

    private String requestIp;

    private LocalDateTime createdAt;
}