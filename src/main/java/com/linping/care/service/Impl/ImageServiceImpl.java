package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.linping.care.entity.ImageEntity;
import com.linping.care.mapper.ImageMapper;
import com.linping.care.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl extends MPJBaseServiceImpl<ImageMapper, ImageEntity> implements ImageService {
    private final ImageMapper imageMapper;

    @Override
    public ImageEntity getByNewsId(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getNewsId, id);
        return imageMapper.selectOne(queryWrapper);
    }

    @Override
    public ImageEntity getByGoodsId(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getGoodsId, id);
        return imageMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ImageEntity> NursingImagesById(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getNursingId, id);
        return imageMapper.selectList(queryWrapper);
    }

    @Override
    public List<ImageEntity> NewsImagesById(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getNewsId, id);
        return imageMapper.selectList(queryWrapper);
    }

    @Override
    public ImageEntity userImageById(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getUserId, id);
        return imageMapper.selectOne(queryWrapper);
    }

    @Override
    public void replaceIp(String ip, String port) {
        // 获取所有图片
        List<ImageEntity> imageEntities = imageMapper.selectList(null);
        // 正则匹配url中的ip部分
        String reg = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)(:\\d{1,5})?";
        for (ImageEntity imageEntity : imageEntities) {
            String url = imageEntity.getSrc();
            if (url != null) {
                String endStr = url.replaceAll(reg, ip + ":" + port);
                // 更新图片url
                imageEntity.setSrc(endStr);
            }
        }
        // 批量更新图片
        imageMapper.updateById(imageEntities);
    }

    @Override
    public ImageEntity getByBedId(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getBedId, id);
        return imageMapper.selectOne(queryWrapper);
    }

    @Override
    public ImageEntity getByEmployeeId(Integer id) {
        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getEmployeeId, id);
        return imageMapper.selectOne(queryWrapper);
    }
}
