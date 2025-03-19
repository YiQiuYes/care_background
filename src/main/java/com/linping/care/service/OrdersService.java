package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.OrdersDTO;
import com.linping.care.entity.OrdersEntity;

import java.util.List;
import java.util.Map;

public interface OrdersService extends MPJBaseService<OrdersEntity> {
    boolean orderInsert(List<OrdersDTO> ordersDTOs, Integer userId);

    Map<String, List<OrdersEntity>> ordersMap(Integer userId);

    List<OrdersDTO> ordersTypeList(String type, Integer status);

    boolean modifyStatusById(Integer id, Integer status);

    List<OrdersDTO> orderStatusList(Integer integer, Integer status);
}
