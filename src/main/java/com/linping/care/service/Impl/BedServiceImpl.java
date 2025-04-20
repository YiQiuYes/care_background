package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.github.yulichang.wrapper.UpdateJoinWrapper;
import com.linping.care.dto.BedDTO;
import com.linping.care.entity.BedEntity;
import com.linping.care.entity.ImageEntity;
import com.linping.care.mapper.BedMapper;
import com.linping.care.service.BedService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.BatchResult;
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

    @Override
    public boolean isAlreadyBooking(Integer ownId) {
        MPJLambdaWrapper<BedEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(BedEntity.class);
        queryWrapper.eq(BedEntity::getOwnId, ownId);
        List<BedEntity> bedEntities = bedMapper.selectJoinList(queryWrapper);
        return !bedEntities.isEmpty();
    }

    @Override
    public List<BedDTO> getBedByUserId(Integer id) {
        MPJLambdaWrapper<BedEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(BedEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, BedDTO::getImageSrc);
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getBedId, BedEntity::getId);
        queryWrapper.eq(BedEntity::getOwnId, id);

        return bedMapper.selectJoinList(BedDTO.class, queryWrapper);
    }

    @Override
    public boolean cancelBookingBedByOwnId(Integer ownId) {
        UpdateJoinWrapper<BedEntity> update = JoinWrappers.update(BedEntity.class);
        update.set(BedEntity::getStatus, 0);
        update.set(BedEntity::getOwnId, null);
        update.eq(BedEntity::getOwnId, ownId);
        return bedMapper.update(update) > 0;
    }
}
