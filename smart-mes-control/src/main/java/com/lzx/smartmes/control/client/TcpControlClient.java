package com.lzx.smartmes.control.client;

import com.lzx.smartmes.device.entity.Device;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

@Component
public class TcpControlClient implements ControlProtocolClient {

    @Override
    public boolean supports(String p) {
        return "TCP".equalsIgnoreCase(p)
                || "MODBUS_TCP".equalsIgnoreCase(p);
    }

    @Override
    public ControlResult command(
            Device d,
            String c,
            String id
    ) {

        try (Socket s = new Socket()) {

            s.connect(
                    new InetSocketAddress(
                            d.getIpAddress(),
                            d.getPort()
                    ),
                    1000
            );

            new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream())
            ).append(
                    "{\"command\":\"" + c
                            + "\",\"commandId\":\"" + id
                            + "\"}\n"
            ).flush();

            return new ControlResult(
                    true,
                    "TCP command sent"
            );

        } catch (IOException e) {

            return new ControlResult(
                    false,
                    "TCP control failed: " + e.getMessage()
            );
        }
    }
}