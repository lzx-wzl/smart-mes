package com.lzx.smartmes.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lzx.smartmes.production.entity.WorkOrder;
import com.lzx.smartmes.production.service.WorkOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * MES 车间工单 API。
 * 根路径：/api/production/work-orders
 *
 * 为兼容现有项目的权限名称，这里暂用：
 * - PRODUCTION_READ：读操作
 * - PRODUCTION_CREATE：所有写操作
 * 后续可在 RBAC 中细分 PRODUCTION_UPDATE / PRODUCTION_DELETE。
 */
@RestController
@RequestMapping("/api/production/work-orders")
@RequiredArgsConstructor
@Validated
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    /** 1. 新建工单。 */
    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTION_CREATE')")
    public ResponseEntity<WorkOrder> create(@Valid @RequestBody CreateRequest request) {
        WorkOrder input = new WorkOrder();
        input.setWorkOrderNo(request.workOrderNo());
        input.setProductionOrderId(request.productionOrderId());
        input.setProcessRouteId(request.processRouteId());
        input.setWorkshopId(request.workshopId());
        input.setPlanQuantity(request.planQuantity());
        input.setCurrentStepNo(request.currentStepNo());
        input.setPlannedStartTime(request.plannedStartTime());
        input.setPlannedEndTime(request.plannedEndTime());

        WorkOrder created = workOrderService.createWorkOrder(input);
        return ResponseEntity.created(
                URI.create("/api/production/work-orders/" + created.getId())
        ).body(created);
    }

    /** 2. 根据主键查看工单。 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTION_READ')")
    public WorkOrder detail(@PathVariable Long id) {
        return workOrderService.getDetail(id);
    }

    /** 3. 分页查询，也支持不传过滤条件直接查询所有工单。 */
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTION_READ')")
    public IPage<WorkOrder> page(
            @RequestParam(defaultValue = "1") @Min(1) long current,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) long size,
            @RequestParam(required = false) Long productionOrderId,
            @RequestParam(required = false) Long workshopId,
            @RequestParam(required = false) String status) {
        return workOrderService.pageWorkOrders(
                current, size, productionOrderId, workshopId, status
        );
    }

    /** 4. 按生产订单查询该订单下全部车间工单。 */
    @GetMapping("/by-production-order/{productionOrderId}")
    @PreAuthorize("hasAuthority('PRODUCTION_READ')")
    public List<WorkOrder> byProductionOrder(@PathVariable Long productionOrderId) {
        return workOrderService.listByProductionOrderId(productionOrderId);
    }

    /** 5. 修改工单基础信息，PUT/PATCH 均接受，只更新传入的非 null 字段。 */
    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @PreAuthorize("hasAuthority('PRODUCTION_CREATE')")
    public WorkOrder update(@PathVariable Long id,
                            @Valid @RequestBody UpdateRequest request) {
        WorkOrder input = new WorkOrder();
        input.setWorkOrderNo(request.workOrderNo());
        input.setProcessRouteId(request.processRouteId());
        input.setWorkshopId(request.workshopId());
        input.setPlanQuantity(request.planQuantity());
        input.setCurrentStepNo(request.currentStepNo());
        input.setPlannedStartTime(request.plannedStartTime());
        input.setPlannedEndTime(request.plannedEndTime());
        return workOrderService.updateWorkOrder(id, input);
    }

    /** 6. 前端更新工单状态，不能直接绕过状态机。 */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PRODUCTION_CREATE')")
    public WorkOrder changeStatus(@PathVariable Long id,
                                  @Valid @RequestBody ChangeStatusRequest request) {
        return workOrderService.changeStatus(id, request.status());
    }

    /** 7. 前端/车间终端生产报工（本次增加的合格数量）。 */
    @PostMapping("/{id}/report")
    @PreAuthorize("hasAuthority('PRODUCTION_CREATE')")
    public WorkOrder report(@PathVariable Long id,
                            @Valid @RequestBody ReportRequest request) {
        return workOrderService.reportQuantity(id, request.quantity());
    }

    /** 8. 删除尚未下达的 CREATED 工单。 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTION_CREATE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 下列 record 放在 Controller 内，避免仅为请求 DTO 再新增四个 Java 文件。
     * 正式企业项目可再将其拆到 production/dto 包。
     */
    public record CreateRequest(
            @NotBlank @Size(max = 64) String workOrderNo,
            @NotNull @Positive Long productionOrderId,
            @NotNull @Positive Long processRouteId,
            @NotNull @Positive Long workshopId,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal planQuantity,
            @Positive Integer currentStepNo,
            LocalDateTime plannedStartTime,
            LocalDateTime plannedEndTime
    ) {
    }

    public record UpdateRequest(
            @Size(min = 1, max = 64) String workOrderNo,
            @Positive Long processRouteId,
            @Positive Long workshopId,
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal planQuantity,
            @Positive Integer currentStepNo,
            LocalDateTime plannedStartTime,
            LocalDateTime plannedEndTime
    ) {
    }

    public record ChangeStatusRequest(@NotBlank String status) {
    }

    public record ReportRequest(
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity
    ) {
    }

    /** 统一返回容易被前端处理的错误结构（成功响应保持原项目对象）。 */
    public record ErrorResponse(int code, String message) {
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler({IllegalStateException.class, DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> conflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + "：" + error.getDefaultMessage())
                .orElse("请求参数校验失败");
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, message));
    }
}
