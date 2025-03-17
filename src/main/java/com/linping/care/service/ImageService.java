package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.ImageEntity;

import java.util.List;

public interface ImageService extends MPJBaseService<ImageEntity> {
    ImageEntity getByNewsId(Integer id);

    ImageEntity getByGoodsId(Integer id);

    List<ImageEntity> NursingImagesById(Integer id);

    List<ImageEntity> NewsImagesById(Integer id);
}
