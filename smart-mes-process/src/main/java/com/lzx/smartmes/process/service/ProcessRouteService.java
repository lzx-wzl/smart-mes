package com.lzx.smartmes.process.service;

import com.lzx.smartmes.process.dto.RouteCreateDTO;
import com.lzx.smartmes.process.dto.StepCreateDTO;
import com.lzx.smartmes.process.entity.ProcessStep;
import com.lzx.smartmes.process.vo.ProcessRouteVO;

import java.util.List;

public interface ProcessRouteService {

    ProcessRouteVO create(RouteCreateDTO dto);

    List<ProcessRouteVO> byProduct(Long productId);

    ProcessStep addStep(Long routeId, StepCreateDTO dto);

    List<ProcessStep> steps(Long routeId);
}