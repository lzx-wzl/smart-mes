package com.lzx.smartmes.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzx.smartmes.production.entity.WorkOrder;

import java.math.BigDecimal;
import java.util.List;


public interface WorkOrderService extends IService<WorkOrder> {

    WorkOrder createWorkOrder(WorkOrder request);

    WorkOrder updateWorkOrder(Long id, WorkOrder request);

    WorkOrder getDetail(Long id);

    IPage<WorkOrder> pageWorkOrders(long current, long size,
                                    Long productionOrderId, Long workshopId,
                                    String status);

    List<WorkOrder> listByProductionOrderId(Long productionOrderId);

    WorkOrder changeStatus(Long id, String targetStatus);

    WorkOrder reportQuantity(Long id, BigDecimal increment);

    void deleteWorkOrder(Long id);
}
