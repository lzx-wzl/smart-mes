package com.lzx.smartmes.control.client;

import com.lzx.smartmes.device.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class OpcUaControlClient implements ControlProtocolClient {

    @Override
    public boolean supports(String p) {
        return "OPC_UA".equalsIgnoreCase(p);
    }

    @Override
    public ControlResult command(
            Device d,
            String c,
            String id
    ) {
        return new ControlResult(
                false,
                "OPC UA adapter is a safe simulator; replace with Eclipse Milo for a real endpoint"
        );
    }
}