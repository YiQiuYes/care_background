package com.linping.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linping.care.entity.ImageEntity;

public interface ImageService extends IService<ImageEntity> {
    ImageEntity getByNewsId(Integer id);
}
