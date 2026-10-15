package com.lzx.smartmes.collector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryPublisher {

    public static final String EXCHANGE =
            "mes.device.exchange";

    private final RabbitTemplate rabbit;

    public void publish(JsonNode payload) {

        if (!payload.hasNonNull("deviceCode")
                || !payload.hasNonNull("timestamp")) {

            throw new IllegalArgumentException(
                    "deviceCode and timestamp required"
            );
        }

        ObjectNode out = (ObjectNode) payload.deepCopy();

        if (!out.hasNonNull("messageId")) {
            out.put(
                    "messageId",
                    UUID.randomUUID().toString()
            );
        }

        String key = out.hasNonNull("alarmCode")
                        && !out.get("alarmCode")
                        .asText()
                        .isBlank()
                        ? "device.alarm"
                        : "device.telemetry";

        rabbit.convertAndSend(
                EXCHANGE,
                key,
                out.toString()
        );

        log.info(
                "telemetry published for {}",
                out.get("deviceCode").asText()
        );
    }
}