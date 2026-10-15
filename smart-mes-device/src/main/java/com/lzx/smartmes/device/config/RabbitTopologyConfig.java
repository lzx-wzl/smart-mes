package com.lzx.smartmes.device.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {

    public static final String EXCHANGE =
            "mes.device.exchange";

    public static final String TELEMETRY_QUEUE =
            "mes.device.telemetry.queue";

    public static final String ALARM_QUEUE =
            "mes.device.alarm.queue";

    public static final String DLQ =
            "mes.device.telemetry.dlq";

    @Bean
    DirectExchange deviceExchange() {
        return new DirectExchange(
                EXCHANGE,
                true,
                false
        );
    }

    @Bean
    Queue telemetryQueue() {
        return QueueBuilder
                .durable(TELEMETRY_QUEUE)
                .deadLetterExchange(
                        EXCHANGE + ".dlx"
                )
                .deadLetterRoutingKey(
                        "device.telemetry.dlq"
                )
                .build();
    }

    @Bean
    Queue alarmQueue() {
        return QueueBuilder
                .durable(ALARM_QUEUE)
                .build();
    }

    @Bean
    Queue telemetryDlq() {
        return QueueBuilder
                .durable(DLQ)
                .build();
    }

    @Bean
    DirectExchange deadExchange() {
        return new DirectExchange(
                EXCHANGE + ".dlx",
                true,
                false
        );
    }

    @Bean
    Binding telemetryBinding() {
        return BindingBuilder
                .bind(telemetryQueue())
                .to(deviceExchange())
                .with("device.telemetry");
    }

    @Bean
    Binding alarmBinding() {
        return BindingBuilder
                .bind(alarmQueue())
                .to(deviceExchange())
                .with("device.alarm");
    }

    @Bean
    Binding dlqBinding() {
        return BindingBuilder
                .bind(telemetryDlq())
                .to(deadExchange())
                .with("device.telemetry.dlq");
    }
}