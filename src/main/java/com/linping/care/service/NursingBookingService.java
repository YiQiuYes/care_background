package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.NursingBookingEntity;

import java.util.HashMap;

public interface NursingBookingService extends MPJBaseService<NursingBookingEntity> {
    /**
     * 获取养老院预约信息
     * @param pageNow 当前页
     * @param pageSize 每页显示数量
     * @return HashMap<String, Object>
     */
    HashMap<String, Object> getNursingBookingList(int pageNow, int pageSize);

    boolean updateNursingBooking(Integer id, Integer status);
}
