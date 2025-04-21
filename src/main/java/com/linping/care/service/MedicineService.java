package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.MedicineEntity;

import java.util.HashMap;

public interface MedicineService extends MPJBaseService<MedicineEntity> {
    HashMap<String, Object> getMedicineList(Integer pageNow, Integer pageSize);
}
