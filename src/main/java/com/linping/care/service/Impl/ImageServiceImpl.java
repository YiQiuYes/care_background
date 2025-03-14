package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.linping.care.entity.ImageEntity;
import com.linping.care.mapper.ImageMapper;
import com.linping.care.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl extends MPJBaseServiceImpl<ImageMapper, ImageEntity> implements ImageService {
    private final ImageMapper imageMapper;

    @Override
    public ImageEntity getByNewsId(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.eq(ImageEntity::getNewsId, id);
        return imageMapper.selectOne(queryWrapper);
    }
}
