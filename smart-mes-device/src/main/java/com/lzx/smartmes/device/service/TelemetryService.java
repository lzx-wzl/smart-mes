package com.lzx.smartmes.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.smartmes.common.constant.RedisKeys;
import com.lzx.smartmes.common.enums.DeviceStatus;
import com.lzx.smartmes.common.exception.BusinessException;
import com.lzx.smartmes.device.dto.TelemetryMessage;
import com.lzx.smartmes.device.entity.Device;
import com.lzx.smartmes.device.entity.DeviceAlarm;
import com.lzx.smartmes.device.entity.DeviceData;
import com.lzx.smartmes.device.mapper.DeviceAlarmMapper;
import com.lzx.smartmes.device.mapper.DeviceDataMapper;
import com.lzx.smartmes.device.mapper.DeviceMapper;
import com.lzx.smartmes.device.vo.DeviceStatusVO;
import com.lzx.smartmes.device.websocket.DeviceStatusWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final DeviceMapper devices;
    private final DeviceDataMapper data;
    private final DeviceAlarmMapper alarms;

    private final StringRedisTemplate redis;
    private final ObjectMapper json;

    private final DeviceStatusWebSocketHandler socket;

    @Transactional
    public boolean consume(TelemetryMessage message) {

        // 1. 消息幂等校验
        Boolean fresh = redis.opsForValue().setIfAbsent(
                RedisKeys.processed(message.getMessageId()),
                "1",
                24,
                TimeUnit.HOURS
        );

        // 已经处理过，直接返回
        if (!Boolean.TRUE.equals(fresh)) {
            return false;
        }

        // 2. 根据设备编码查询设备
        Device device = devices.selectOne(
                new LambdaQueryWrapper<Device>()
                        .eq(
                                Device::getDeviceCode,
                                message.getDeviceCode()
                        )
        );

        if (device == null) {
            throw new BusinessException(
                    "unknown device code: " + message.getDeviceCode()
            );
        }

        // 3. 保存设备采集历史数据
        DeviceData history = new DeviceData();

        history.setDeviceId(device.getId());
        history.setTemperature(message.getTemperature());
        history.setPressure(message.getPressure());
        history.setSpeed(message.getSpeed());
        history.setRunningStatus(message.getStatus());
        history.setAlarmCode(message.getAlarmCode());
        history.setCollectTime(message.getTimestamp());

        data.insert(history);

        // 4. 更新设备当前状态
        device.setStatus(
                DeviceStatus.valueOf(message.getStatus())
        );

        device.setLastOnlineTime(
                LocalDateTime.now()
        );

        devices.updateById(device);

        // 5. 构造设备实时状态 VO
        DeviceStatusVO status = new DeviceStatusVO(
                device.getId(),
                message.getStatus(),
                message.getTemperature(),
                message.getPressure(),
                message.getSpeed(),
                message.getTimestamp()
        );

        // 6. 更新 Redis + WebSocket 推送
        try {
            String body =
                    json.writeValueAsString(status);

            redis.opsForValue().set(
                    RedisKeys.deviceStatus(device.getId()),
                    body,
                    5,
                    TimeUnit.MINUTES
            );

            socket.broadcast(body);

        } catch (Exception e) {
            throw new BusinessException(
                    "cannot serialize device state"
            );
        }

        // 7. 如果 PLC 上报报警信息，则保存报警记录
        if (message.getAlarmCode() != null
                && !message.getAlarmCode().isBlank()) {

            DeviceAlarm alarm = new DeviceAlarm();

            alarm.setDeviceId(device.getId());
            alarm.setAlarmCode(message.getAlarmCode());
            alarm.setAlarmLevel("WARN");
            alarm.setAlarmMessage(
                    "PLC reported " + message.getAlarmCode()
            );
            alarm.setAlarmTime(message.getTimestamp());
            alarm.setStatus("OPEN");

            alarms.insert(alarm);
        }

        return true;
    }
}