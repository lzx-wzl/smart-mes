package com.lzx.smartmes.device.controller;

import com.lzx.smartmes.common.result.Result;
import com.lzx.smartmes.device.dto.DeviceCreateDTO;
import com.lzx.smartmes.device.entity.Device;
import com.lzx.smartmes.device.entity.DeviceAlarm;
import com.lzx.smartmes.device.service.DeviceService;
import com.lzx.smartmes.device.vo.DeviceStatusVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService service;

    @PostMapping
    @PreAuthorize("hasAuthority('DEVICE_CONTROL')")
    public Result<Device> create(
            @Valid @RequestBody DeviceCreateDTO dto
    ) {
        return Result.ok(
                service.create(dto)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DEVICE_READ')")
    public Result<List<Device>> list() {
        return Result.ok(
                service.list()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DEVICE_READ')")
    public Result<Device> get(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.get(id)
        );
    }

    @GetMapping("/{id}/status")
    public Result<DeviceStatusVO> status(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.status(id)
        );
    }

    @GetMapping("/{id}/alarms")
    public Result<List<DeviceAlarm>> alarms(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.alarms(id)
        );
    }

    @PostMapping("/{id}/maintenance/claim")
    @PreAuthorize("hasAuthority('DEVICE_CONTROL')")
    public Result<Boolean> claim(
            @PathVariable Long id,
            @RequestHeader("X-Request-Id") String requestId
    ) {
        return Result.ok(
                service.claimMaintenance(id, requestId)
        );
    }
}