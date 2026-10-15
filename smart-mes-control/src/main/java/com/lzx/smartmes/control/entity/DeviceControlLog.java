package com.lzx.smartmes.control.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mes_device_control_log")
public class DeviceControlLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long deviceId;

    private String command;

    private String commandId;

    private String result;

    private String message;

    private LocalDateTime createdAt;
}