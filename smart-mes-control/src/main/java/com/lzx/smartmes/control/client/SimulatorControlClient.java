package com.lzx.smartmes.control.client;

import com.lzx.smartmes.device.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class SimulatorControlClient implements ControlProtocolClient {

    @Override
    public boolean supports(String p) {
        return "SIMULATOR".equalsIgnoreCase(p);
    }

    @Override
    public ControlResult command(
            Device d,
            String c,
            String id
    ) {
        return new ControlResult(
                true,
                "simulator accepted " + c + " command " + id
        );
    }
}