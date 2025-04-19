package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.BedDTO;
import com.linping.care.entity.BedEntity;
import com.linping.care.entity.ImageEntity;
import com.linping.care.entity.NursingBookingEntity;
import com.linping.care.mapper.BedMapper;
import com.linping.care.service.BedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BedServiceImpl extends MPJBaseServiceImpl<BedMapper, BedEntity> implements BedService {
    private final BedMapper bedMapper;


    @Override
    public HashMap<String, Object> getBedList(int pageNow, int pageSize, Integer ownNursingId) {
        MPJLambdaWrapper<BedEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(BedEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, BedDTO::getImageSrc);

        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getBedId, BedEntity::getId);
        if (ownNursingId != null) {
            queryWrapper.eq(BedEntity::getNursingId, ownNursingId);
        }
        HashMap<String, Object> result = new HashMap<>();
        Page<BedDTO> page = new Page<>(pageNow, pageSize);
        page = bedMapper.selectJoinPage(page, BedDTO.class, queryWrapper);
        List<BedDTO> records = page.getRecords();
        records.sort(Comparator.comparing(BedDTO::getStatus, Comparator.nullsLast(Integer::compareTo)));
        result.put("pages", page.getPages());
        result.put("total", page.getTotal());
        result.put("records", records);
        return result;
    }
}
