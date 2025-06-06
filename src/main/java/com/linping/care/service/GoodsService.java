package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.GoodsDTO;
import com.linping.care.entity.GoodsEntity;

import java.util.HashMap;

public interface GoodsService extends MPJBaseService<GoodsEntity> {
    /**
     * 插入商品
     * @param goodsDTO 商品信息
     * @return 是否插入成功
     */
    GoodsEntity insertGoods(GoodsDTO goodsDTO);

    /**
     * 更新商品
     * @param goodsDTO 商品信息
     * @return 是否插入成功
     */
    boolean updateGoods(GoodsDTO goodsDTO);

    /**
     * 获取商品列表
     * @param type 商品类型
     * @param pageNow 当前页码
     * @param pageSize 每页大小
     * @return 商品列表
     */
    HashMap<String, Object> getGoodsList(String type, int pageNow, int pageSize);
}
