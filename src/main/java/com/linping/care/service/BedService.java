package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.BedEntity;

import java.util.HashMap;

public interface BedService extends MPJBaseService<BedEntity> {
    HashMap<String, Object> getBedList(int pageNow, int pageSize, Integer ownNursingId);
}
