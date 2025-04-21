package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.MedicineDTO;
import com.linping.care.entity.ImageEntity;
import com.linping.care.entity.MedicineEntity;
import com.linping.care.mapper.MedicineMapper;
import com.linping.care.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl extends MPJBaseServiceImpl<MedicineMapper, MedicineEntity> implements MedicineService {
    private final MedicineMapper medicineMapper;

    @Override
    public HashMap<String, Object> getMedicineList(Integer pageNow, Integer pageSize) {
        MPJLambdaWrapper<MedicineEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(MedicineEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, MedicineDTO::getEmployeeImage);

        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getEmployeeId, MedicineEntity::getEmployeeId);
        HashMap<String, Object> result = new HashMap<>();
        Page<MedicineDTO> page = new Page<>(pageNow, pageSize);
        page = medicineMapper.selectJoinPage(page, MedicineDTO.class, queryWrapper);
        List<MedicineDTO> records = page.getRecords();
        result.put("pages", page.getPages());
        result.put("total", page.getTotal());
        result.put("records", records);
        return result;
    }
}
