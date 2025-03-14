package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.ImageEntity;

public interface ImageService extends MPJBaseService<ImageEntity> {
    ImageEntity getByNewsId(Integer id);
}
