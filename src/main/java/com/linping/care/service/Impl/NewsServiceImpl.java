package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.NewsDTO;
import com.linping.care.entity.ImageEntity;
import com.linping.care.entity.NewsEntity;
import com.linping.care.mapper.NewsMapper;
import com.linping.care.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl extends MPJBaseServiceImpl<NewsMapper, NewsEntity> implements NewsService {
    private final NewsMapper newsMapper;

    @Override
    public HashMap<String, Object> getNewsList(String type, int pageNow, int pageSize) {
        MPJLambdaWrapper<NewsEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectFilter(NewsEntity.class, i -> !i.getColumn().equals("content"));
        queryWrapper.selectAs(ImageEntity::getSrc, NewsDTO::getImageSrc);
        if(!type.equals("all")) {
            queryWrapper.eq("type", type);
        }

        queryWrapper.orderByDesc("create_time");
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getNewsId, NewsEntity::getId);

        HashMap<String, Object> result = new HashMap<>();
        Page<NewsDTO> page = new Page<>(pageNow, pageSize);
        page = newsMapper.selectJoinPage(page, NewsDTO.class, queryWrapper);
        result.put("pages", page.getPages());
        result.put("total", page.getTotal());
        result.put("records", page.getRecords());
        return result;
    }

    @Override
    public boolean insertNews(NewsEntity newsEntity) {
        return newsMapper.insert(newsEntity) > 0;
    }

    @Override
    public List<NewsDTO> getNewsSlide() {
        // 随机从数据库中获取不同类型的新闻
        MPJLambdaWrapper<NewsEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectFilter(NewsEntity.class, i -> !i.getColumn().equals("content"));
        queryWrapper.selectAs(ImageEntity::getSrc, NewsDTO::getImageSrc);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 3");
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getNewsId, NewsEntity::getId);
        return newsMapper.selectJoinList(NewsDTO.class, queryWrapper);
    }

    @Override
    public NewsDTO getNewsDetailById(Integer id) {
        MPJLambdaWrapper<NewsEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(NewsEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, NewsDTO::getImageSrc);
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getNewsId, NewsEntity::getId);
        queryWrapper.eq(NewsEntity::getId, id);

        return newsMapper.selectJoinOne(NewsDTO.class, queryWrapper);
    }
}
