package com.lzx.smartmes.collector.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.lzx.smartmes.collector.service.TelemetryPublisher;
import com.lzx.smartmes.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collector")
@RequiredArgsConstructor
public class CollectorController {

    private final TelemetryPublisher publisher;

    @PostMapping("/telemetry")
    public Result<Void> telemetry(@RequestBody JsonNode body) {

        publisher.publish(body);

        return Result.ok();
    }
}