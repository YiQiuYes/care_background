package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.CartDTO;
import com.linping.care.dto.GoodsDTO;
import com.linping.care.entity.CartEntity;
import com.linping.care.entity.GoodsEntity;
import com.linping.care.entity.ImageEntity;
import com.linping.care.mapper.CartMapper;
import com.linping.care.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl extends MPJBaseServiceImpl<CartMapper, CartEntity> implements CartService {
    private final CartMapper cartMapper;

    @Override
    public HashMap<String, Object> getCartList(Integer userId, int pageNow, int pageSize) {
        MPJLambdaWrapper<CartEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(CartEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, GoodsDTO::getImageSrc);
        queryWrapper.selectAs(GoodsEntity::getName, GoodsDTO::getName);
        queryWrapper.selectAs(GoodsEntity::getDescription, GoodsDTO::getDescription);
        queryWrapper.selectAs(GoodsEntity::getType, GoodsDTO::getType);
        queryWrapper.selectAs(GoodsEntity::getPrice, GoodsDTO::getPrice);
        queryWrapper.selectAs(GoodsEntity::getIsActive, GoodsDTO::getIsActive);
        queryWrapper.selectAs(GoodsEntity::getCreateTime, GoodsDTO::getCreateTime);
        queryWrapper.leftJoin(GoodsEntity.class, GoodsEntity::getId, CartEntity::getGoodsId);
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getGoodsId, GoodsEntity::getId);
        queryWrapper.eq(CartEntity::getUserId, userId);

        HashMap<String, Object> result = new HashMap<>();
        Page<GoodsDTO> page = new Page<>(pageNow, pageSize);
        page = cartMapper.selectJoinPage(page, GoodsDTO.class, queryWrapper);
        List<GoodsDTO> records = page.getRecords();

        ArrayList<CartDTO> list = new ArrayList<>();
        for (GoodsDTO goodsDTO : records) {
            CartDTO cartDTO = new CartDTO();
            cartDTO.setGoodsDTO(goodsDTO);

            CartEntity cartEntity = cartMapper.selectById(goodsDTO.getId());
            cartDTO.setCount(cartEntity.getCount());
            cartDTO.setId(cartEntity.getId());
            list.add(cartDTO);
        }

        result.put("pages", page.getPages());
        result.put("total", page.getTotal());
        result.put("records", list);
        return result;
    }
}
