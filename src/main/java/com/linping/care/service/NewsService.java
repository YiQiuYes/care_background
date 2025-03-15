package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.NewsDTO;
import com.linping.care.entity.NewsEntity;

import java.util.HashMap;
import java.util.List;

public interface NewsService extends MPJBaseService<NewsEntity> {
    HashMap<String, Object> getNewsList(String type, int pageNow, int pageSize);

    boolean insertNews(NewsEntity newsEntity);

    List<NewsDTO> getNewsSlide();

    NewsDTO getNewsDetailById(Integer id);
}
