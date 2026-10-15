package com.lzx.smartmes.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzx.smartmes.production.dto.ProductionOrderCreateDTO;
import com.lzx.smartmes.production.vo.ProductionOrderVO;

public interface ProductionOrderService {

    ProductionOrderVO create(
            ProductionOrderCreateDTO dto
    );

    ProductionOrderVO get(
            Long id
    );

    Page<ProductionOrderVO> page(
            long current,
            long size
    );

    ProductionOrderVO plan(
            Long id
    );

    ProductionOrderVO release(
            Long id
    );

    ProductionOrderVO start(
            Long id
    );

    ProductionOrderVO pause(
            Long id
    );

    ProductionOrderVO resume(
            Long id
    );

    ProductionOrderVO complete(
            Long id
    );

    ProductionOrderVO cancel(
            Long id
    );
}