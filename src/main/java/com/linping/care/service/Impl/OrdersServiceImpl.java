package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.OrdersDTO;
import com.linping.care.entity.GoodsEntity;
import com.linping.care.entity.OrdersEntity;
import com.linping.care.mapper.OrdersMapper;
import com.linping.care.service.GoodsService;
import com.linping.care.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersServiceImpl extends MPJBaseServiceImpl<OrdersMapper, OrdersEntity> implements OrdersService {
    private final OrdersMapper ordersMapper;

    private final GoodsService goodsService;

    @Override
    @Transactional
    public boolean orderInsert(List<OrdersDTO> ordersDTOs, Integer userId) {
        ArrayList<OrdersEntity> list = new ArrayList<>();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        String uuid = UUID.randomUUID().toString();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(OrdersDTO ordersDTO : ordersDTOs) {
            OrdersEntity ordersEntity = new OrdersEntity();
            ordersEntity.setUserId(userId);
            ordersEntity.setGoodsId(ordersDTO.getGoodsId());
            ordersEntity.setAddress(ordersDTO.getAddress());
            ordersEntity.setPhone(ordersDTO.getPhone());
            ordersEntity.setStatus(1);
            ordersEntity.setCreateTime(now);
            ordersEntity.setOrderId(uuid);
            ordersEntity.setCount(ordersDTO.getCount());

            GoodsEntity goodsEntity = goodsService.getById(ordersDTO.getGoodsId());
            BigDecimal count = new BigDecimal(ordersDTO.getCount());
            totalPrice = totalPrice.add(goodsEntity.getPrice().multiply(count));
            list.add(ordersEntity);
        }

        for (OrdersEntity ordersEntity : list) {
            ordersEntity.setPrice(totalPrice);
        }

        List<BatchResult> insert = ordersMapper.insert(list);
        return !insert.isEmpty();
    }

    @Override
    public Map<String, List<OrdersEntity>> ordersMap(Integer userId) {
        MPJLambdaQueryWrapper<OrdersEntity> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(OrdersEntity.class);
        wrapper.eq(OrdersEntity::getUserId, userId);
        wrapper.orderByDesc(OrdersEntity::getCreateTime);
        wrapper.orderByDesc(OrdersEntity::getOrderId);
        List<OrdersEntity> ordersEntities = ordersMapper.selectList(wrapper);

        // 根据 OrdersEntity.getOrderId() 分组
        return ordersEntities.stream().collect(Collectors.groupingBy(OrdersEntity::getOrderId));
    }

    @Override
    public List<OrdersDTO> ordersTypeList(String type, Integer status) {
        MPJLambdaWrapper<OrdersEntity> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(OrdersEntity.class);
        if (status != null) {
            wrapper.eq(OrdersEntity::getStatus, status);
        }
        wrapper.leftJoin(GoodsEntity.class, GoodsEntity::getId, OrdersEntity::getGoodsId);
        if ("common".equals(type)) {
            wrapper.eq(GoodsEntity::getType, type);
        }
        List<OrdersEntity> ordersEntities = ordersMapper.selectJoinList(OrdersEntity.class, wrapper);
        ArrayList<OrdersDTO> list = new ArrayList<>();

        for (OrdersEntity ordersEntity : ordersEntities) {
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setId(ordersEntity.getId());
            ordersDTO.setGoodsId(ordersEntity.getGoodsId());

            GoodsEntity goodsEntity = goodsService.getById(ordersDTO.getGoodsId());
            ordersDTO.setPrice(goodsEntity.getPrice());
            ordersDTO.setAddress(ordersEntity.getAddress());
            ordersDTO.setCreateTime(ordersEntity.getCreateTime().getTime());
            ordersDTO.setStatus(ordersEntity.getStatus());
            ordersDTO.setPhone(ordersEntity.getPhone());
            ordersDTO.setCount(ordersEntity.getCount());
            list.add(ordersDTO);
        }

        return list;
    }

    @Override
    public boolean modifyStatusById(Integer id, Integer status) {
        OrdersEntity ordersEntity = ordersMapper.selectById(id);
        if (ordersEntity == null) {
            return false;
        } else {
            ordersEntity.setStatus(status);
            return ordersMapper.updateById(ordersEntity) > 0;
        }
    }

    @Override
    public List<OrdersDTO> orderStatusList(Integer userId, Integer status) {
        MPJLambdaWrapper<OrdersEntity> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(OrdersEntity.class);
        wrapper.eq(OrdersEntity::getUserId, userId);
        wrapper.eq(OrdersEntity::getStatus, status);
        List<OrdersEntity> ordersEntities = ordersMapper.selectList(wrapper);
        ArrayList<OrdersDTO> list = new ArrayList<>();
        for (OrdersEntity ordersEntity : ordersEntities) {
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setId(ordersEntity.getId());
            ordersDTO.setGoodsId(ordersEntity.getGoodsId());
            ordersDTO.setPrice(ordersEntity.getPrice());
            ordersDTO.setAddress(ordersEntity.getAddress());
            ordersDTO.setCreateTime(ordersEntity.getCreateTime().getTime());
            ordersDTO.setStatus(ordersEntity.getStatus());
            ordersDTO.setPhone(ordersEntity.getPhone());
            ordersDTO.setCount(ordersEntity.getCount());
            list.add(ordersDTO);
        }

        return list;
    }
}
