package com.lzx.smartmes.device.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.smartmes.device.config.RabbitTopologyConfig;
import com.lzx.smartmes.device.dto.TelemetryMessage;
import com.lzx.smartmes.device.service.TelemetryService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryConsumer {

    private final ObjectMapper json;

    private final TelemetryService service;

    @RabbitListener(queues = RabbitTopologyConfig.TELEMETRY_QUEUE, ackMode = "MANUAL")
    public void consume(Message message, Channel channel) throws Exception {

        long deliveryTag =
                message.getMessageProperties().getDeliveryTag();

        try {
            TelemetryMessage telemetry =
                    json.readValue(
                            message.getBody(),
                            TelemetryMessage.class
                    );

            service.consume(telemetry);

            channel.basicAck(
                    deliveryTag,
                    false
            );

        } catch (Exception e) {

            log.error(
                    "telemetry consume failed",
                    e
            );

            channel.basicNack(
                    deliveryTag,
                    false,
                    false
            );
        }
    }
}