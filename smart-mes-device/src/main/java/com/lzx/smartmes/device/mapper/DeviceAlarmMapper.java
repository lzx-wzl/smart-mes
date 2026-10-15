package com.lzx.smartmes.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzx.smartmes.device.entity.DeviceAlarm;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceAlarmMapper
        extends BaseMapper<DeviceAlarm> {
}