package com.lzx.smartmes.process.controller;

import com.lzx.smartmes.common.result.Result;
import com.lzx.smartmes.process.dto.RouteCreateDTO;
import com.lzx.smartmes.process.dto.StepCreateDTO;
import com.lzx.smartmes.process.entity.ProcessStep;
import com.lzx.smartmes.process.service.ProcessRouteService;
import com.lzx.smartmes.process.vo.ProcessRouteVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/process/routes")
@RequiredArgsConstructor
public class ProcessRouteController {

    private final ProcessRouteService service;

    @PostMapping
    @PreAuthorize("hasAuthority('PROCESS_MANAGE')")
    public Result<ProcessRouteVO> create(
            @Valid @RequestBody RouteCreateDTO d
    ) {
        return Result.ok(service.create(d));
    }

    @GetMapping("/product/{productId}")
    public Result<List<ProcessRouteVO>> byProduct(
            @PathVariable Long productId
    ) {
        return Result.ok(service.byProduct(productId));
    }

    @PostMapping("/{routeId}/steps")
    @PreAuthorize("hasAuthority('PROCESS_MANAGE')")
    public Result<ProcessStep> addStep(
            @PathVariable Long routeId,
            @Valid @RequestBody StepCreateDTO d
    ) {
        return Result.ok(service.addStep(routeId, d));
    }

    @GetMapping("/{routeId}/steps")
    public Result<List<ProcessStep>> steps(
            @PathVariable Long routeId
    ) {
        return Result.ok(service.steps(routeId));
    }
}