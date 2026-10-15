package com.lzx.smartmes.plcsimulator.controller;

import com.lzx.smartmes.plcsimulator.service.PlcSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
public class SimulatorController {

    private final PlcSimulator plc;

    @PostMapping("/emit/{deviceCode}")
    public Map<String, Object> emit(
            @PathVariable String deviceCode
    ) {
        return plc.payload(deviceCode);
    }
}