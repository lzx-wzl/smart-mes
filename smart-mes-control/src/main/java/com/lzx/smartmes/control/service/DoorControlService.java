package com.lzx.smartmes.control.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzx.smartmes.auth.service.CustomUserDetailsService;
import com.lzx.smartmes.auth.service.PermissionService;
import com.lzx.smartmes.common.exception.BusinessException;
import com.lzx.smartmes.control.entity.Door;
import com.lzx.smartmes.control.entity.DoorPermission;
import com.lzx.smartmes.control.entity.DoorControlLog;
import com.lzx.smartmes.control.mapper.DoorMapper;
import com.lzx.smartmes.control.mapper.DoorPermissionMapper;
import com.lzx.smartmes.control.mapper.DoorControlLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoorControlService {

    private final DoorMapper doors;

    private final DoorPermissionMapper permissions;

    private final DoorControlLogMapper logs;

    private final CustomUserDetailsService users;

    private final PermissionService access;

    @Transactional
    public boolean open(Long doorId, String username, String ip) {

        var u = users.current(username);

        DoorControlLog audit = new DoorControlLog();

        audit.setUserId(u.getId());
        audit.setDoorId(doorId);
        audit.setAction("OPEN");
        audit.setRequestIp(ip);
        audit.setCreatedAt(LocalDateTime.now());

        try {

            if (!access.hasPermission(u.getId(), "DOOR_OPEN")) {
                throw new AccessDeniedException(
                        "DOOR_OPEN permission required"
                );
            }

            Door d = doors.selectById(doorId);

            if (d == null) {
                throw new BusinessException("door not found");
            }

            LocalDateTime now = LocalDateTime.now();

            boolean allowed = permissions.selectCount(
                    new LambdaQueryWrapper<DoorPermission>()
                            .eq(DoorPermission::getDoorId, doorId)
                            .eq(DoorPermission::getUserId, u.getId())
                            .eq(DoorPermission::getEnabled, true)
                            .le(DoorPermission::getStartTime, now)
                            .ge(DoorPermission::getEndTime, now)
            ) > 0;

            if (!allowed) {
                throw new AccessDeniedException(
                        "no door access in current time window"
                );
            }

            if (!"ONLINE".equals(d.getStatus())) {
                throw new BusinessException(
                        "door is not online"
                );
            }

            audit.setResult("SUCCESS");
            audit.setReason("simulator door opened");

            logs.insert(audit);

            return true;

        } catch (RuntimeException e) {

            audit.setResult("FAILED");
            audit.setReason(e.getMessage());

            logs.insert(audit);

            log.warn(
                    "door operation denied for user {} door {}",
                    username,
                    doorId
            );

            throw e;
        }
    }
}