package com.lzx.smartmes.production.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzx.smartmes.common.enums.ProductionOrderStatus;
import com.lzx.smartmes.common.exception.BusinessException;
import com.lzx.smartmes.production.dto.ProductionOrderCreateDTO;
import com.lzx.smartmes.production.entity.ProductionOrder;
import com.lzx.smartmes.production.mapper.ProductionOrderMapper;
import com.lzx.smartmes.production.service.ProductionOrderService;
import com.lzx.smartmes.production.vo.ProductionOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderServiceImpl implements ProductionOrderService {

 private final ProductionOrderMapper mapper;

 @Override
 @Transactional
 public ProductionOrderVO create(
         ProductionOrderCreateDTO dto
 ) {

  if (dto.getPlannedEndTime()
          .isBefore(dto.getPlannedStartTime())) {

   throw new BusinessException(
           "planned end must be after start"
   );
  }

  ProductionOrder order =
          new ProductionOrder();

  order.setOrderNo(
          "MO-" + UUID.randomUUID()
                  .toString()
                  .substring(0, 8)
                  .toUpperCase()
  );

  order.setProductId(
          dto.getProductId()
  );

  order.setPlanQuantity(
          dto.getPlanQuantity()
  );

  order.setCompletedQuantity(
          BigDecimal.ZERO
  );

  order.setPriority(
          dto.getPriority()
  );

  order.setStatus(
          ProductionOrderStatus.CREATED
  );

  order.setPlannedStartTime(
          dto.getPlannedStartTime()
  );

  order.setPlannedEndTime(
          dto.getPlannedEndTime()
  );

  order.setVersion(0);

  mapper.insert(order);

  return vo(order);
 }

 @Override
 public ProductionOrderVO get(Long id) {
  return vo(
          required(id)
  );
 }

 @Override
 public Page<ProductionOrderVO> page(
         long current,
         long size
 ) {

  Page<ProductionOrder> page =
          mapper.selectPage(
                  Page.of(current, size),
                  null
          );

  Page<ProductionOrderVO> result =
          Page.of(
                  current,
                  size,
                  page.getTotal()
          );

  result.setRecords(
          page.getRecords()
                  .stream()
                  .map(this::vo)
                  .toList()
  );

  return result;
 }

 @Override
 @Transactional
 public ProductionOrderVO plan(Long id) {
  return transition(
          id,
          ProductionOrderStatus.CREATED,
          ProductionOrderStatus.PLANNED
  );
 }

 @Override
 @Transactional
 public ProductionOrderVO release(Long id) {
  return transition(
          id,
          ProductionOrderStatus.PLANNED,
          ProductionOrderStatus.RELEASED
  );
 }

 @Override
 @Transactional
 public ProductionOrderVO start(Long id) {
  return transition(
          id,
          ProductionOrderStatus.RELEASED,
          ProductionOrderStatus.RUNNING
  );
 }

 @Override
 @Transactional
 public ProductionOrderVO pause(Long id) {
  return transition(
          id,
          ProductionOrderStatus.RUNNING,
          ProductionOrderStatus.PAUSED
  );
 }

 @Override
 @Transactional
 public ProductionOrderVO resume(Long id) {
  return transition(
          id,
          ProductionOrderStatus.PAUSED,
          ProductionOrderStatus.RUNNING
  );
 }

 @Override
 @Transactional
 public ProductionOrderVO complete(Long id) {
  return transition(
          id,
          ProductionOrderStatus.RUNNING,
          ProductionOrderStatus.COMPLETED
  );
 }

 @Override
 @Transactional
 public ProductionOrderVO cancel(Long id) {

  ProductionOrder order =
          required(id);

  if (order.getStatus()
          != ProductionOrderStatus.CREATED
          && order.getStatus()
          != ProductionOrderStatus.PLANNED) {

   throw new BusinessException(
           "Order cannot be cancelled from current state: "
                   + order.getStatus()
   );
  }

  return saveTransition(
          order,
          ProductionOrderStatus.CANCELLED
  );
 }

 private ProductionOrderVO transition(
         Long id,
         ProductionOrderStatus from,
         ProductionOrderStatus to
 ) {

  ProductionOrder order =
          required(id);

  if (order.getStatus() != from) {

   throw new BusinessException(
           "Order cannot transition from "
                   + order.getStatus()
                   + " to "
                   + to
   );
  }

  return saveTransition(
          order,
          to
  );
 }

 private ProductionOrderVO saveTransition(
         ProductionOrder order,
         ProductionOrderStatus target
 ) {

  order.setStatus(target);

  if (target == ProductionOrderStatus.RUNNING
          && order.getActualStartTime() == null) {

   order.setActualStartTime(
           LocalDateTime.now()
   );
  }

  if (target == ProductionOrderStatus.COMPLETED) {

   order.setActualEndTime(
           LocalDateTime.now()
   );
  }

  if (mapper.updateById(order) != 1) {

   throw new BusinessException(
           "Order changed concurrently, please retry"
   );
  }

  log.info(
          "production order {} changed to {}",
          order.getOrderNo(),
          target
  );

  return vo(order);
 }

 private ProductionOrder required(Long id) {

  ProductionOrder order =
          mapper.selectById(id);

  if (order == null) {

   throw new BusinessException(
           "Production order not found"
   );
  }

  return order;
 }

 private ProductionOrderVO vo(
         ProductionOrder order
 ) {

  return ProductionOrderVO.builder()
          .id(order.getId())
          .orderNo(order.getOrderNo())
          .erpOrderNo(order.getErpOrderNo())
          .productId(order.getProductId())
          .planQuantity(order.getPlanQuantity())
          .completedQuantity(order.getCompletedQuantity())
          .status(order.getStatus())
          .version(order.getVersion())
          .plannedStartTime(order.getPlannedStartTime())
          .plannedEndTime(order.getPlannedEndTime())
          .build();
 }
}