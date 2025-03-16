package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.linping.care.entity.NursingBookingEntity;
import com.linping.care.mapper.NursingBookingMapper;
import com.linping.care.service.NursingBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NursingBookingServiceImpl extends MPJBaseServiceImpl<NursingBookingMapper, NursingBookingEntity> implements NursingBookingService {
    private final NursingBookingMapper nursingBookingMapper;
}
