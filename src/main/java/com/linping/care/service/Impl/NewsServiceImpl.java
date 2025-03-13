package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linping.care.entity.NewsEntity;
import com.linping.care.mapper.NewsMapper;
import com.linping.care.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl extends ServiceImpl<NewsMapper, NewsEntity> implements NewsService {
    private final NewsMapper newsMapper;

    @Override
    public HashMap<String, Object> getNewsList(String type, int pageNow, int pageSize) {
        // SELECT * FROM news limit #{pageNow},#{pageSize}
        QueryWrapper<NewsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(NewsEntity.class, i -> !i.getColumn().equals("content"));
        if(!type.equals("all")) {
            queryWrapper.eq("type", type);
        }

        queryWrapper.orderByDesc("create_time");

        HashMap<String, Object> result = new HashMap<>();
        Page<NewsEntity> page = new Page<>(pageNow, pageSize);
        page = newsMapper.selectPage(page, queryWrapper);
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
    public ArrayList<Object> getNewsSlide() {
        // 随机从数据库中获取不同类型的新闻
        QueryWrapper<NewsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(NewsEntity.class, i -> !i.getColumn().equals("content"));
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 3");
        ArrayList<NewsEntity> newsEntityList = (ArrayList<NewsEntity>) newsMapper.selectList(queryWrapper);

        return new ArrayList<>(newsEntityList);
    }
}
