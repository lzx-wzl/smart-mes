package com.lzx.smartmes.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.smartmes.common.constant.RedisKeys;
import com.lzx.smartmes.common.enums.DeviceStatus;
import com.lzx.smartmes.common.exception.BusinessException;
import com.lzx.smartmes.device.dto.DeviceCreateDTO;
import com.lzx.smartmes.device.entity.Device;
import com.lzx.smartmes.device.entity.DeviceAlarm;
import com.lzx.smartmes.device.mapper.DeviceAlarmMapper;
import com.lzx.smartmes.device.mapper.DeviceMapper;
import com.lzx.smartmes.device.service.DeviceService;
import com.lzx.smartmes.device.vo.DeviceStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper devices;

    private final DeviceAlarmMapper alarms;

    private final StringRedisTemplate redis;

    private final ObjectMapper json;

    @Override
    public Device create(DeviceCreateDTO dto) {

        if (devices.selectCount(
                new LambdaQueryWrapper<Device>()
                        .eq(
                                Device::getDeviceCode,
                                dto.getDeviceCode()
                        )
        ) > 0) {
            throw new BusinessException(
                    "device code already exists"
            );
        }

        Device device = new Device();

        device.setDeviceCode(dto.getDeviceCode());
        device.setDeviceName(dto.getDeviceName());
        device.setDeviceType(dto.getDeviceType());
        device.setWorkshopId(dto.getWorkshopId());
        device.setProtocol(dto.getProtocol());
        device.setIpAddress(dto.getIpAddress());
        device.setPort(dto.getPort());
        device.setStatus(DeviceStatus.OFFLINE);

        devices.insert(device);

        return device;
    }

    @Override
    public List<Device> list() {
        return devices.selectList(null);
    }

    @Override
    public Device get(Long id) {

        Device device = devices.selectById(id);

        if (device == null) {
            throw new BusinessException(
                    "device not found"
            );
        }

        return device;
    }

    @Override
    public DeviceStatusVO status(Long id) {

        get(id);

        String value = redis.opsForValue()
                .get(
                        RedisKeys.deviceStatus(id)
                );

        try {
            if (value != null) {
                return json.readValue(
                        value,
                        DeviceStatusVO.class
                );
            }
        } catch (Exception ignored) {
        }

        Device device = get(id);

        return new DeviceStatusVO(
                id,
                device.getStatus().name(),
                null,
                null,
                null,
                device.getLastOnlineTime()
        );
    }

    @Override
    public List<DeviceAlarm> alarms(Long id) {

        return alarms.selectList(
                new LambdaQueryWrapper<DeviceAlarm>()
                        .eq(
                                DeviceAlarm::getDeviceId,
                                id
                        )
                        .orderByDesc(
                                DeviceAlarm::getAlarmTime
                        )
        );
    }

    @Override
    public boolean claimMaintenance(
            Long id,
            String requestId
    ) {

        get(id);

        String key =
                RedisKeys.maintenanceLock(id);

        Boolean success =
                redis.opsForValue()
                        .setIfAbsent(
                                key,
                                requestId,
                                10,
                                TimeUnit.MINUTES
                        );

        return Boolean.TRUE.equals(success);
    }

    public void releaseMaintenance(
            Long id,
            String requestId
    ) {

        redis.execute(
                new DefaultRedisScript<>(
                        "if redis.call('get', KEYS[1]) == ARGV[1] " +
                                "then return redis.call('del', KEYS[1]) " +
                                "else return 0 end",
                        Long.class
                ),
                List.of(
                        RedisKeys.maintenanceLock(id)
                ),
                requestId
        );
    }
}