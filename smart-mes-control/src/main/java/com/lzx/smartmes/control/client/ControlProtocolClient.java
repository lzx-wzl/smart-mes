package com.lzx.smartmes.control.client;

import com.lzx.smartmes.device.entity.Device;

public interface ControlProtocolClient {

    boolean supports(String protocol);

    ControlResult command(
            Device device,
            String command,
            String commandId
    );

    record ControlResult(
            boolean success,
            String message
    ) {
    }
}