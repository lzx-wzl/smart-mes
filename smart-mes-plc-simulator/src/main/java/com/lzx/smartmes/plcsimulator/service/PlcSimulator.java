package com.lzx.smartmes.plcsimulator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class PlcSimulator {

    private final ObjectMapper json;

    private final String mode;

    private final String host;

    private final String http;

    private final int tcpPort;

    private final Random random =
            new Random();

    private final List<String> codes =
            List.of(
                    "CNC-001",
                    "CNC-002",
                    "ROBOT-001"
            );

    public PlcSimulator(
            ObjectMapper json,
            @Value("${smart-mes.simulator.mode:TCP}")
            String mode,
            @Value("${smart-mes.collector.host:localhost}")
            String host,
            @Value("${smart-mes.collector.tcp-port:9009}")
            int tcpPort,
            @Value("${smart-mes.collector.http-url:http://localhost:9006/api/collector/telemetry}")
            String http
    ) {
        this.json = json;
        this.mode = mode;
        this.host = host;
        this.tcpPort = tcpPort;
        this.http = http;
    }

    @Scheduled(
            fixedDelayString =
                    "${smart-mes.simulator.interval-ms:3000}"
    )
    public void emit() {

        for (String code : codes) {

            try {
                Map<String, Object> payload =
                        payload(code);

                if ("HTTP".equalsIgnoreCase(mode)) {

                    RestClient.create()
                            .post()
                            .uri(http)
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(payload)
                            .retrieve()
                            .toBodilessEntity();

                } else {

                    tcp(payload);
                }

            } catch (Exception e) {

                log.warn(
                        "simulator delivery failed; will reconnect next cycle: {}",
                        e.getMessage()
                );
            }
        }
    }

    public Map<String, Object> payload(
            String code
    ) {

        Map<String, Object> payload =
                new LinkedHashMap<>();

        payload.put(
                "messageId",
                UUID.randomUUID().toString()
        );

        payload.put(
                "deviceCode",
                code
        );

        payload.put(
                "temperature",
                BigDecimal.valueOf(
                                65 + random.nextDouble() * 20
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );

        payload.put(
                "pressure",
                BigDecimal.valueOf(
                                1 + random.nextDouble()
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );

        payload.put(
                "speed",
                BigDecimal.valueOf(
                        1000 + random.nextInt(800)
                )
        );

        payload.put(
                "status",
                "RUNNING"
        );

        payload.put(
                "alarmCode",
                random.nextInt(20) == 0
                        ? "TEMP_HIGH"
                        : null
        );

        payload.put(
                "timestamp",
                LocalDateTime.now().toString()
        );

        return payload;
    }

    private void tcp(
            Map<String, Object> payload
    ) throws IOException {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            host,
                            tcpPort
                    ),
                    1000
            );

            OutputStream out =
                    socket.getOutputStream();

            out.write(
                    (
                            json.writeValueAsString(payload)
                                    + "\n"
                    ).getBytes()
            );

            out.flush();
        }
    }
}