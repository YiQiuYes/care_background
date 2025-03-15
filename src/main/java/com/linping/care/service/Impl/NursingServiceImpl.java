package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.linping.care.entity.NursingEntity;
import com.linping.care.mapper.NursingMapper;
import com.linping.care.service.NursingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NursingServiceImpl extends MPJBaseServiceImpl<NursingMapper, NursingEntity> implements NursingService {
    private final NursingMapper nursingMapper;

}
