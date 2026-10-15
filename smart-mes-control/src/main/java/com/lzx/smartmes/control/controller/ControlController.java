package com.lzx.smartmes.control.controller;

import com.lzx.smartmes.common.result.Result;
import com.lzx.smartmes.control.client.ControlProtocolClient;
import com.lzx.smartmes.control.service.DoorControlService;
import com.lzx.smartmes.control.service.DeviceCommandService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/control")
@RequiredArgsConstructor
public class ControlController {

    private final DoorControlService doors;

    private final DeviceCommandService devices;

    @PostMapping("/doors/{doorId}/open")
    @PreAuthorize("hasAuthority('DOOR_OPEN')")
    public Result<Boolean> open(
            @PathVariable Long doorId,
            Authentication a,
            HttpServletRequest r
    ) {
        return Result.ok(
                doors.open(
                        doorId,
                        a.getName(),
                        r.getRemoteAddr()
                )
        );
    }

    @PostMapping("/devices/{id}/{command:start|stop|reset}")
    @PreAuthorize("hasAuthority('DEVICE_CONTROL')")
    public Result<ControlProtocolClient.ControlResult> command(
            @PathVariable Long id,
            @PathVariable String command,
            Authentication a
    ) {
        return Result.ok(
                devices.execute(
                        id,
                        command.toUpperCase(),
                        a.getName()
                )
        );
    }
}