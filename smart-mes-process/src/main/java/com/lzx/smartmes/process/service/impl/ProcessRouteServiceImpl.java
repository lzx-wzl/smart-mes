package com.lzx.smartmes.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzx.smartmes.common.exception.BusinessException;
import com.lzx.smartmes.process.dto.RouteCreateDTO;
import com.lzx.smartmes.process.dto.StepCreateDTO;
import com.lzx.smartmes.process.entity.ProcessRoute;
import com.lzx.smartmes.process.entity.ProcessStep;
import com.lzx.smartmes.process.mapper.ProcessRouteMapper;
import com.lzx.smartmes.process.mapper.ProcessStepMapper;
import com.lzx.smartmes.process.service.ProcessRouteService;
import com.lzx.smartmes.process.vo.ProcessRouteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessRouteServiceImpl implements ProcessRouteService {

    private final ProcessRouteMapper routes;

    private final ProcessStepMapper steps;

    @Transactional
    public ProcessRouteVO create(RouteCreateDTO d) {

        Integer version = d.getRouteVersion();

        if (routes.selectCount(
                new LambdaQueryWrapper<ProcessRoute>()
                        .eq(ProcessRoute::getRouteCode, d.getRouteCode())
                        .eq(ProcessRoute::getRouteVersion, version)
        ) > 0) {
            throw new BusinessException("route version already exists");
        }

        ProcessRoute r = new ProcessRoute();

        r.setRouteCode(d.getRouteCode());
        r.setRouteName(d.getRouteName());
        r.setProductId(d.getProductId());
        r.setRouteVersion(version);
        r.setStatus("ACTIVE");

        routes.insert(r);

        return vo(r);
    }

    public List<ProcessRouteVO> byProduct(Long productId) {

        return routes.selectList(
                        new LambdaQueryWrapper<ProcessRoute>()
                                .eq(ProcessRoute::getProductId, productId)
                ).stream()
                .map(this::vo)
                .toList();
    }

    @Transactional
    public ProcessStep addStep(Long id, StepCreateDTO d) {

        if (routes.selectById(id) == null) {
            throw new BusinessException("process route not found");
        }

        if (d.getStepNo() % 10 != 0) {
            throw new BusinessException("stepNo must be a multiple of 10");
        }

        if (steps.selectCount(
                new LambdaQueryWrapper<ProcessStep>()
                        .eq(ProcessStep::getRouteId, id)
                        .eq(ProcessStep::getStepNo, d.getStepNo())
        ) > 0) {
            throw new BusinessException("step number exists");
        }

        ProcessStep s = new ProcessStep();

        s.setRouteId(id);
        s.setStepNo(d.getStepNo());
        s.setProcessCode(d.getProcessCode());
        s.setProcessName(d.getProcessName());
        s.setWorkstationId(d.getWorkstationId());
        s.setStandardDuration(d.getStandardDuration());
        s.setParameterJson(d.getParameterJson());

        steps.insert(s);

        return s;
    }

    public List<ProcessStep> steps(Long id) {

        return steps.selectList(
                new LambdaQueryWrapper<ProcessStep>()
                        .eq(ProcessStep::getRouteId, id)
                        .orderByAsc(ProcessStep::getStepNo)
        );
    }

    private ProcessRouteVO vo(ProcessRoute r) {

        return new ProcessRouteVO(
                r.getId(),
                r.getProductId(),
                r.getRouteCode(),
                r.getRouteName(),
                r.getStatus(),
                r.getRouteVersion()
        );
    }
}