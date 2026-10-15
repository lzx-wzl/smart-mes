package com.lzx.smartmes.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzx.smartmes.common.enums.DeviceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mes_device")
public class Device {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long workshopId;

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private String ipAddress;

    private String protocol;

    private Integer port;

    private DeviceStatus status;

    private LocalDateTime lastOnlineTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}