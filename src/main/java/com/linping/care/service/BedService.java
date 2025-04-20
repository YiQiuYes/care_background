package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.BedDTO;
import com.linping.care.entity.BedEntity;

import java.util.HashMap;
import java.util.List;

public interface BedService extends MPJBaseService<BedEntity> {
    HashMap<String, Object> getBedList(int pageNow, int pageSize, Integer ownNursingId);

    boolean isAlreadyBooking(Integer ownId);

    List<BedDTO> getBedByUserId(Integer id);

    boolean cancelBookingBedByOwnId(Integer ownId);
}
