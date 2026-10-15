package com.lzx.smartmes.device.service;

import com.lzx.smartmes.device.dto.DeviceCreateDTO;
import com.lzx.smartmes.device.entity.Device;
import com.lzx.smartmes.device.entity.DeviceAlarm;
import com.lzx.smartmes.device.vo.DeviceStatusVO;

import java.util.List;

public interface DeviceService {

    Device create(DeviceCreateDTO dto);

    List<Device> list();

    Device get(Long id);

    DeviceStatusVO status(Long id);

    List<DeviceAlarm> alarms(Long id);

    boolean claimMaintenance(
            Long deviceId,
            String requestId
    );
}