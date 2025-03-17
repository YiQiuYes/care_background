package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.NursingBookingDTO;
import com.linping.care.entity.NursingBookingEntity;
import com.linping.care.entity.NursingEntity;
import com.linping.care.entity.UserEntity;
import com.linping.care.mapper.NursingBookingMapper;
import com.linping.care.service.NursingBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NursingBookingServiceImpl extends MPJBaseServiceImpl<NursingBookingMapper, NursingBookingEntity> implements NursingBookingService {
    private final NursingBookingMapper nursingBookingMapper;

    @Override
    public HashMap<String, Object> getNursingBookingList(int pageNow, int pageSize) {
        MPJLambdaWrapper<NursingBookingEntity> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(NursingBookingEntity.class);
        wrapper.orderByAsc(NursingBookingEntity::getTime);
        wrapper.selectAs(UserEntity::getUsername, NursingBookingDTO::getUserName);
        wrapper.leftJoin(NursingEntity.class, NursingEntity::getId, NursingBookingEntity::getNursingId);
        wrapper.selectAs(NursingEntity::getName, NursingBookingDTO::getNursingName);
        wrapper.leftJoin(UserEntity.class, UserEntity::getId, NursingBookingEntity::getUserId);

        HashMap<String, Object> result = new HashMap<>();
        Page<NursingBookingDTO> page = new Page<>(pageNow, pageSize);
        page = nursingBookingMapper.selectJoinPage(page, NursingBookingDTO.class, wrapper);
        result.put("pages", page.getPages());
        result.put("total", page.getTotal());
        result.put("records", page.getRecords());
        return result;
    }

    @Override
    public boolean updateNursingBooking(Integer id, Integer status) {
        UpdateWrapper<NursingBookingEntity> wrapper = new UpdateWrapper<>();
        if (status > 1) {
            status = 1;
        }
        wrapper.set("status", status);
        wrapper.eq("id", id);
        return nursingBookingMapper.update(null, wrapper) > 0;
    }
}
