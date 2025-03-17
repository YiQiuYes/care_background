package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.GoodsDTO;
import com.linping.care.entity.GoodsEntity;
import com.linping.care.entity.ImageEntity;
import com.linping.care.mapper.GoodsMapper;
import com.linping.care.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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

    @Override
    public HashMap<String, Object> getGoodsList(String type, int pageNow, int pageSize) {
        MPJLambdaWrapper<GoodsEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(GoodsEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, GoodsDTO::getImageSrc);
        if(!type.equals("common")) {
            queryWrapper.eq("type", type);
        }

        queryWrapper.orderByDesc("create_time");
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getGoodsId, GoodsEntity::getId);

        HashMap<String, Object> result = new HashMap<>();
        Page<GoodsDTO> page = new Page<>(pageNow, pageSize);
        page = goodsMapper.selectJoinPage(page, GoodsDTO.class, queryWrapper);
        result.put("pages", page.getPages());
        result.put("total", page.getTotal());
        result.put("records", page.getRecords());
        return result;
    }
}
