package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
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
}
