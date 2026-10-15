package com.lzx.smartmes.control.service;

import com.lzx.smartmes.auth.service.CustomUserDetailsService;
import com.lzx.smartmes.common.enums.DeviceStatus;
import com.lzx.smartmes.common.exception.BusinessException;
import com.lzx.smartmes.control.client.ControlProtocolClient;
import com.lzx.smartmes.control.entity.DeviceControlLog;
import com.lzx.smartmes.control.mapper.DeviceControlLogMapper;
import com.lzx.smartmes.device.entity.Device;
import com.lzx.smartmes.device.mapper.DeviceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceCommandService {

    private final DeviceMapper devices;

    private final DeviceControlLogMapper logs;

    private final List<ControlProtocolClient> clients;

    private final CustomUserDetailsService users;

    @Transactional
    public ControlProtocolClient.ControlResult execute(
            Long deviceId,
            String command,
            String username
    ) {

        Device d = devices.selectById(deviceId);

        if (d == null) {
            throw new BusinessException("device not found");
        }

        if (d.getStatus() == DeviceStatus.FAULT
                || d.getStatus() == DeviceStatus.MAINTENANCE) {

            throw new BusinessException(
                    "Device is in " + d.getStatus() + " state"
            );
        }

        if ("START".equals(command)
                && d.getStatus() != DeviceStatus.STOPPED
                && d.getStatus() != DeviceStatus.ONLINE) {

            throw new BusinessException(
                    "device cannot start from " + d.getStatus()
            );
        }

        String id = UUID.randomUUID().toString();

        ControlProtocolClient c = clients.stream()
                .filter(x -> x.supports(d.getProtocol()))
                .findFirst()
                .orElseThrow(
                        () -> new BusinessException(
                                "no control adapter for protocol"
                        )
                );

        ControlProtocolClient.ControlResult r = c.command(
                d,
                command,
                id
        );

        DeviceControlLog l = new DeviceControlLog();

        l.setUserId(users.current(username).getId());
        l.setDeviceId(deviceId);
        l.setCommand(command);
        l.setCommandId(id);
        l.setResult(r.success() ? "SUCCESS" : "FAILED");
        l.setMessage(r.message());
        l.setCreatedAt(LocalDateTime.now());

        logs.insert(l);

        if (r.success()) {

            if ("START".equals(command)) {
                d.setStatus(DeviceStatus.RUNNING);
            }

            if ("STOP".equals(command)) {
                d.setStatus(DeviceStatus.STOPPED);
            }

            devices.updateById(d);
        }

        return r;
    }
}