package com.lzx.smartmes.integration.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzx.smartmes.common.enums.ProductionOrderStatus;
import com.lzx.smartmes.common.exception.BusinessException;
import com.lzx.smartmes.integration.dto.ErpOrderDTO;
import com.lzx.smartmes.integration.entity.ProductRef;
import com.lzx.smartmes.integration.mapper.ProductRefMapper;
import com.lzx.smartmes.production.entity.ProductionOrder;
import com.lzx.smartmes.production.mapper.ProductionOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ErpOrderService {

    private final ProductionOrderMapper orders;

    private final ProductRefMapper products;

    @Transactional
    public ProductionOrder receive(ErpOrderDTO dto) {

        ProductionOrder exists = orders.selectOne(
                new LambdaQueryWrapper<ProductionOrder>()
                        .eq(
                                ProductionOrder::getErpOrderNo,
                                dto.getErpOrderNo()
                        )
        );

        if (exists != null) {
            return exists;
        }

        ProductRef product = products.selectOne(
                new LambdaQueryWrapper<ProductRef>()
                        .eq(
                                ProductRef::getProductCode,
                                dto.getProductCode()
                        )
        );

        if (product == null) {
            throw new BusinessException(
                    "product not found: " + dto.getProductCode()
            );
        }

        if (dto.getPlannedEndTime()
                .isBefore(dto.getPlannedStartTime())) {

            throw new BusinessException(
                    "planned end must be after start"
            );
        }

        ProductionOrder order = new ProductionOrder();

        order.setOrderNo(
                "MO-ERP-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        order.setErpOrderNo(dto.getErpOrderNo());

        order.setProductId(product.getId());

        order.setPlanQuantity(dto.getQuantity());

        order.setCompletedQuantity(BigDecimal.ZERO);

        order.setPriority("NORMAL");

        order.setStatus(ProductionOrderStatus.CREATED);

        order.setPlannedStartTime(dto.getPlannedStartTime());

        order.setPlannedEndTime(dto.getPlannedEndTime());

        order.setVersion(0);

        orders.insert(order);

        return order;
    }
}