package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.EmployeeEntity;

import java.util.HashMap;

public interface EmployeeService extends MPJBaseService<EmployeeEntity> {
    HashMap<String, Object> getEmployeeList(int pageNow, int pageSize, Integer ownNursingId);

    boolean cancelBookingEmployeeByUserId(Integer userId);

    HashMap<String, Object> getPersonBookingList(Integer id);
}
