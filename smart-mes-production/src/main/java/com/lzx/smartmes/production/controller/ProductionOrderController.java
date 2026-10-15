package com.lzx.smartmes.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzx.smartmes.common.result.Result;
import com.lzx.smartmes.production.dto.ProductionOrderCreateDTO;
import com.lzx.smartmes.production.service.ProductionOrderService;
import com.lzx.smartmes.production.vo.ProductionOrderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production/orders")
@Tag(name = "生产订单")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService service;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTION_CREATE')")
    public Result<ProductionOrderVO> create(
            @Valid @RequestBody ProductionOrderCreateDTO dto
    ) {
        return Result.ok(
                service.create(dto)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTION_READ')")
    public Result<ProductionOrderVO> get(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.get(id)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTION_READ')")
    public Result<Page<ProductionOrderVO>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size
    ) {
        return Result.ok(
                service.page(current, size)
        );
    }

    @PostMapping("/{id}/plan")
    @PreAuthorize("hasAuthority('PRODUCTION_EXECUTE')")
    public Result<ProductionOrderVO> plan(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.plan(id)
        );
    }

    @PostMapping("/{id}/release")
    @PreAuthorize("hasAuthority('PRODUCTION_EXECUTE')")
    public Result<ProductionOrderVO> release(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.release(id)
        );
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('PRODUCTION_EXECUTE')")
    public Result<ProductionOrderVO> start(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.start(id)
        );
    }

    @PostMapping("/{id}/pause")
    @PreAuthorize("hasAuthority('PRODUCTION_EXECUTE')")
    public Result<ProductionOrderVO> pause(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.pause(id)
        );
    }

    @PostMapping("/{id}/resume")
    @PreAuthorize("hasAuthority('PRODUCTION_EXECUTE')")
    public Result<ProductionOrderVO> resume(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.resume(id)
        );
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('PRODUCTION_EXECUTE')")
    public Result<ProductionOrderVO> complete(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.complete(id)
        );
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('PRODUCTION_EXECUTE')")
    public Result<ProductionOrderVO> cancel(
            @PathVariable Long id
    ) {
        return Result.ok(
                service.cancel(id)
        );
    }
}