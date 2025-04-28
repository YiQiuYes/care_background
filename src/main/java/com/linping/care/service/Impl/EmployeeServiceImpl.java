package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.github.yulichang.wrapper.UpdateJoinWrapper;
import com.linping.care.dto.EmployeeDTO;
import com.linping.care.entity.EmployeeEntity;
import com.linping.care.entity.ImageEntity;
import com.linping.care.mapper.EmployeeMapper;
import com.linping.care.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends MPJBaseServiceImpl<EmployeeMapper, EmployeeEntity> implements EmployeeService {
    private final EmployeeMapper employeeMapper;

    @Override
    public HashMap<String, Object> getEmployeeList(int pageNow, int pageSize, Integer ownNursingId) {
        MPJLambdaWrapper<EmployeeEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(EmployeeEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, EmployeeDTO::getImage);

        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getEmployeeId, EmployeeEntity::getId);
        if (ownNursingId != null) {
            queryWrapper.eq(EmployeeEntity::getNursingId, ownNursingId);
        }
        HashMap<String, Object> result = new HashMap<>();
        Page<EmployeeDTO> page = new Page<>(pageNow, pageSize);
        page = employeeMapper.selectJoinPage(page, EmployeeDTO.class, queryWrapper);
        List<EmployeeDTO> records = page.getRecords();
        records.sort(Comparator.comparing(EmployeeDTO::getStatus, Comparator.nullsLast(Integer::compareTo)));
        result.put("pages", page.getPages());
        result.put("total", page.getTotal());
        result.put("records", records);
        return result;
    }

    @Override
    public boolean cancelBookingEmployeeByUserId(Integer userId) {
        UpdateJoinWrapper<EmployeeEntity> update = JoinWrappers.update(EmployeeEntity.class);
        update.set(EmployeeEntity::getStatus, 0);
        update.set(EmployeeEntity::getUserId, null);
        update.eq(EmployeeEntity::getUserId, userId);
        return employeeMapper.update(update) > 0;
    }

    @Override
    public HashMap<String, Object> getPersonBookingList(Integer id) {
        HashMap<String, Object> result = new HashMap<>();
        MPJLambdaWrapper<EmployeeEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(EmployeeEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, EmployeeDTO::getImage);
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getEmployeeId, EmployeeEntity::getId);
        queryWrapper.eq(EmployeeEntity::getUserId, id);

        List<EmployeeDTO> dtoList = employeeMapper.selectJoinList(EmployeeDTO.class, queryWrapper);
        result.put("records", dtoList);
        result.put("total", dtoList.size());
        return result;
    }
}
