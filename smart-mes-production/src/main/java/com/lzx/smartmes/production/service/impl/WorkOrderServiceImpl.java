package com.lzx.smartmes.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzx.smartmes.production.entity.WorkOrder;
import com.lzx.smartmes.production.mapper.WorkOrderMapper;
import com.lzx.smartmes.production.service.WorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class WorkOrderServiceImpl
        extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements WorkOrderService {


    private static final Set<String> ALL_STATUSES = Set.of(
            "CREATED", "PLANNED", "RELEASED", "RUNNING",
            "PAUSED", "COMPLETED", "CANCELLED"
    );


    private static final Map<String, Set<String>> NEXT_STATUSES = Map.of(
            "CREATED", Set.of("PLANNED", "CANCELLED"),
            "PLANNED", Set.of("RELEASED", "CANCELLED"),
            "RELEASED", Set.of("RUNNING", "CANCELLED"),
            "RUNNING", Set.of("PAUSED", "COMPLETED"),
            "PAUSED", Set.of("RUNNING"),
            "COMPLETED", Set.of(),
            "CANCELLED", Set.of()
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder createWorkOrder(WorkOrder request) {
        if (request == null) {
            throw new IllegalArgumentException("工单内容不能为空");
        }


        WorkOrder entity = new WorkOrder();
        entity.setWorkOrderNo(normalizeWorkOrderNo(request.getWorkOrderNo()));
        entity.setProductionOrderId(request.getProductionOrderId());
        entity.setProcessRouteId(request.getProcessRouteId());
        entity.setWorkshopId(request.getWorkshopId());
        entity.setPlanQuantity(request.getPlanQuantity());
        entity.setCurrentStepNo(request.getCurrentStepNo() == null ? 1 : request.getCurrentStepNo());
        entity.setPlannedStartTime(request.getPlannedStartTime());
        entity.setPlannedEndTime(request.getPlannedEndTime());
        entity.setCompletedQuantity(BigDecimal.ZERO);
        entity.setStatus("CREATED");

        validateWorkOrder(entity);
        ensureWorkOrderNoAvailable(entity.getWorkOrderNo(), null);

        if (!save(entity)) {
            throw new IllegalStateException("创建工单失败");
        }
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder updateWorkOrder(Long id, WorkOrder request) {
        if (request == null) {
            throw new IllegalArgumentException("修改内容不能为空");
        }
        WorkOrder entity = getForUpdate(id);
        if (!Set.of("CREATED", "PLANNED").contains(entity.getStatus())) {
            throw new IllegalStateException("工单已下达或执行，不能修改基础信息");
        }

        // 此方法是局部更新：未传(null)的字段保持数据库原值。
        if (request.getWorkOrderNo() != null) {
            String newNo = normalizeWorkOrderNo(request.getWorkOrderNo());
            ensureWorkOrderNoAvailable(newNo, entity.getId());
            entity.setWorkOrderNo(newNo);
        }
        if (request.getProcessRouteId() != null) {
            entity.setProcessRouteId(request.getProcessRouteId());
        }
        if (request.getWorkshopId() != null) {
            entity.setWorkshopId(request.getWorkshopId());
        }
        if (request.getPlanQuantity() != null) {
            entity.setPlanQuantity(request.getPlanQuantity());
        }
        if (request.getCurrentStepNo() != null) {
            entity.setCurrentStepNo(request.getCurrentStepNo());
        }
        if (request.getPlannedStartTime() != null) {
            entity.setPlannedStartTime(request.getPlannedStartTime());
        }
        if (request.getPlannedEndTime() != null) {
            entity.setPlannedEndTime(request.getPlannedEndTime());
        }

        // 生产订单ID、状态、完成数量、实际时间均不能从普通编辑接口修改。
        validateWorkOrder(entity);
        if (entity.getCompletedQuantity() != null
                && entity.getCompletedQuantity().compareTo(entity.getPlanQuantity()) > 0) {
            throw new IllegalArgumentException("计划数量不能小于已完成数量");
        }

        if (!updateById(entity)) {
            throw new IllegalStateException("修改工单失败");
        }
        return entity;
    }

    @Override
    public WorkOrder getDetail(Long id) {
        checkPositiveId(id, "工单ID");
        WorkOrder entity = getById(id);
        if (entity == null) {
            throw new NoSuchElementException("工单不存在，ID=" + id);
        }
        return entity;
    }

    @Override
    public IPage<WorkOrder> pageWorkOrders(long current, long size,
                                           Long productionOrderId,
                                           Long workshopId, String status) {
        if (current < 1 || size < 1 || size > 100) {
            throw new IllegalArgumentException("页码必须大于0，且每页最多100条");
        }
        if (productionOrderId != null) {
            checkPositiveId(productionOrderId, "生产订单ID");
        }
        if (workshopId != null) {
            checkPositiveId(workshopId, "车间ID");
        }
        LambdaQueryWrapper<WorkOrder> wrapper = Wrappers.lambdaQuery(WorkOrder.class);
        wrapper.eq(productionOrderId != null, WorkOrder::getProductionOrderId, productionOrderId)
                .eq(workshopId != null, WorkOrder::getWorkshopId, workshopId)
                .orderByDesc(WorkOrder::getId);

        if (status != null && !status.isBlank()) {
            wrapper.eq(WorkOrder::getStatus, normalizeStatus(status));
        }
        return baseMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public List<WorkOrder> listByProductionOrderId(Long productionOrderId) {
        checkPositiveId(productionOrderId, "生产订单ID");
        return list(Wrappers.<WorkOrder>lambdaQuery()
                .eq(WorkOrder::getProductionOrderId, productionOrderId)
                .orderByDesc(WorkOrder::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder changeStatus(Long id, String targetStatus) {
        String target = normalizeStatus(targetStatus);
        WorkOrder entity = getForUpdate(id);
        String current = entity.getStatus();
        if (!ALL_STATUSES.contains(current)) {
            throw new IllegalStateException("数据库中存在未知工单状态：" + current);
        }
        if (current.equals(target)) {
            return entity; // 重复提交同一个状态不重复执行，便于前端安全重试。
        }
        if (!NEXT_STATUSES.get(current).contains(target)) {
            throw new IllegalStateException("不允许工单状态从 " + current + " 变为 " + target);
        }

        if ("RUNNING".equals(target) && entity.getActualStartTime() == null) {
            entity.setActualStartTime(LocalDateTime.now());
        }
        if ("COMPLETED".equals(target)) {
            BigDecimal done = entity.getCompletedQuantity() == null
                    ? BigDecimal.ZERO : entity.getCompletedQuantity();
            if (entity.getPlanQuantity() == null
                    || done.compareTo(entity.getPlanQuantity()) != 0) {
                throw new IllegalStateException("只有完成数量等于计划数量时，工单才允许完工");
            }
            entity.setActualEndTime(LocalDateTime.now());
        }
        entity.setStatus(target);
        if (!updateById(entity)) {
            throw new IllegalStateException("修改工单状态失败");
        }
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder reportQuantity(Long id, BigDecimal increment) {
        if (increment == null || increment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("本次报工数量必须大于0");
        }
        WorkOrder entity = getForUpdate(id);
        if (!"RUNNING".equals(entity.getStatus())) {
            throw new IllegalStateException("只有 RUNNING 状态的工单可以报工");
        }
        BigDecimal previous = entity.getCompletedQuantity() == null
                ? BigDecimal.ZERO : entity.getCompletedQuantity();
        BigDecimal result = previous.add(increment);
        if (entity.getPlanQuantity() == null
                || result.compareTo(entity.getPlanQuantity()) > 0) {
            throw new IllegalStateException("累计完工数量不能超过计划数量");
        }
        entity.setCompletedQuantity(result);
        if (!updateById(entity)) {
            throw new IllegalStateException("生产报工失败");
        }
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkOrder(Long id) {
        WorkOrder entity = getForUpdate(id);
        if (!"CREATED".equals(entity.getStatus())) {
            throw new IllegalStateException("只允许删除 CREATED 状态的工单，其他工单应保留生产记录");
        }
        if (!removeById(id)) {
            throw new IllegalStateException("删除工单失败");
        }
    }

    /**
     * 在 MySQL/InnoDB 的事务里使用行锁，避免两次并发报工相互覆盖。
     * 不需要修改现有 WorkOrderMapper：它继承的 BaseMapper 已具备 selectOne 能力。
     */
    private WorkOrder getForUpdate(Long id) {
        checkPositiveId(id, "工单ID");
        WorkOrder entity = baseMapper.selectOne(
                Wrappers.<WorkOrder>lambdaQuery()
                        .eq(WorkOrder::getId, id)
                        .last("FOR UPDATE")
        );
        if (entity == null) {
            throw new NoSuchElementException("工单不存在，ID=" + id);
        }
        return entity;
    }

    private void ensureWorkOrderNoAvailable(String workOrderNo, Long exceptId) {
        LambdaQueryWrapper<WorkOrder> wrapper = Wrappers.lambdaQuery(WorkOrder.class)
                .eq(WorkOrder::getWorkOrderNo, workOrderNo);
        if (exceptId != null) {
            wrapper.ne(WorkOrder::getId, exceptId);
        }
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new IllegalStateException("工单编号已存在：" + workOrderNo);
        }
    }

    private static String normalizeWorkOrderNo(String workOrderNo) {
        if (workOrderNo == null || workOrderNo.isBlank()) {
            throw new IllegalArgumentException("工单编号不能为空");
        }
        String result = workOrderNo.trim();
        if (result.length() > 64) {
            throw new IllegalArgumentException("工单编号长度不能超过64");
        }
        return result;
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("工单状态不能为空");
        }
        String result = status.trim().toUpperCase(Locale.ROOT);
        if (!ALL_STATUSES.contains(result)) {
            throw new IllegalArgumentException("未知工单状态：" + status);
        }
        return result;
    }

    private static void validateWorkOrder(WorkOrder entity) {
        checkPositiveId(entity.getProductionOrderId(), "生产订单ID");
        checkPositiveId(entity.getProcessRouteId(), "工艺路线ID");
        checkPositiveId(entity.getWorkshopId(), "车间ID");
        if (entity.getPlanQuantity() == null
                || entity.getPlanQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("计划数量必须大于0");
        }
        if (entity.getCurrentStepNo() == null || entity.getCurrentStepNo() <= 0) {
            throw new IllegalArgumentException("当前工序号必须大于0");
        }
        LocalDateTime start = entity.getPlannedStartTime();
        LocalDateTime end = entity.getPlannedEndTime();
        if (start != null && end != null && !end.isAfter(start)) {
            throw new IllegalArgumentException("计划结束时间必须晚于计划开始时间");
        }
    }

    private static void checkPositiveId(Long id, String label) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(label + "必须为正整数");
        }
    }
}
