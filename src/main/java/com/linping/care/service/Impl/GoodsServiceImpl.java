package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.linping.care.dto.GoodsDTO;
import com.linping.care.entity.GoodsEntity;
import com.linping.care.mapper.GoodsMapper;
import com.linping.care.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoodsServiceImpl extends MPJBaseServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {
    private final GoodsMapper goodsMapper;

    @Override
    public GoodsEntity insertGoods(GoodsDTO goodsDTO) {
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setName(goodsDTO.getName());
        goodsEntity.setDescription(goodsDTO.getDescription());
        goodsEntity.setType(goodsDTO.getType());
        goodsEntity.setPrice(goodsDTO.getPrice());
        goodsEntity.setIsActive(goodsDTO.getIsActive());
        goodsEntity.setCreateTime(goodsDTO.getCreateTime());

        boolean isSuccess = goodsMapper.insert(goodsEntity) == 1;
        if (!isSuccess) {
            return null;
        }

        return goodsEntity;
    }

    @Override
    public boolean updateGoods(GoodsDTO goodsDTO) {
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setId(goodsDTO.getId());
        goodsEntity.setName(goodsDTO.getName());
        goodsEntity.setDescription(goodsDTO.getDescription());
        goodsEntity.setType(goodsDTO.getType());
        goodsEntity.setPrice(goodsDTO.getPrice());
        goodsEntity.setIsActive(goodsDTO.getIsActive());
        goodsEntity.setCreateTime(goodsDTO.getCreateTime());

        return goodsMapper.updateById(goodsEntity) == 1;
    }
}
