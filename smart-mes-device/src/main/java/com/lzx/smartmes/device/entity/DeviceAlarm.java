package com.lzx.smartmes.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mes_device_alarm")
public class DeviceAlarm {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long deviceId;

    private String alarmCode;

    private String alarmLevel;

    private String alarmMessage;

    private String status;

    private LocalDateTime alarmTime;
}