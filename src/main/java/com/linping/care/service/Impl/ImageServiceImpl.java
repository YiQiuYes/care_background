package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linping.care.entity.ImageEntity;
import com.linping.care.mapper.ImageMapper;
import com.linping.care.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageEntity> implements ImageService {

}
