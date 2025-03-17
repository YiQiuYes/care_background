package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.GoodsDTO;
import com.linping.care.entity.GoodsEntity;

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
}
