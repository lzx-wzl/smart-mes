package com.lzx.smartmes.integration.controller;

import com.lzx.smartmes.common.result.Result;
import com.lzx.smartmes.integration.dto.ErpOrderDTO;
import com.lzx.smartmes.integration.service.ErpOrderService;
import com.lzx.smartmes.integration.service.SrmClient;
import com.lzx.smartmes.integration.vo.SrmMaterialResponse;
import com.lzx.smartmes.production.entity.ProductionOrder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final ErpOrderService erp;

    private final SrmClient srm;

    @PostMapping("/erp/orders")
    @PreAuthorize("hasAuthority('PRODUCTION_CREATE')")
    public Result<ProductionOrder> receive(
            @Valid @RequestBody ErpOrderDTO d
    ) {
        return Result.ok(erp.receive(d));
    }

    @GetMapping("/srm/materials/{code}/stock")
    public Result<SrmMaterialResponse> stock(
            @PathVariable String code
    ) {
        return Result.ok(srm.stock(code));
    }
}