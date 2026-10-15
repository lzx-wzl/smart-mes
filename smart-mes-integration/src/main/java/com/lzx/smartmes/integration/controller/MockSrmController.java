package com.lzx.smartmes.integration.controller;

import com.lzx.smartmes.integration.vo.SrmMaterialResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/mock/srm")
public class MockSrmController {

    @GetMapping("/material/{code}")
    public SrmMaterialResponse material(
            @PathVariable String code
    ) {
        return new SrmMaterialResponse(
                code,
                "SUP-001",
                new BigDecimal("5000")
        );
    }
}