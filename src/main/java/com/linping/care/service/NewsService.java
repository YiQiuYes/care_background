package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.NewsDTO;
import com.linping.care.entity.NewsEntity;

import java.util.ArrayList;
import java.util.HashMap;

public interface NewsService extends MPJBaseService<NewsEntity> {
    HashMap<String, Object> getNewsList(String type, int pageNow, int pageSize);

    boolean insertNews(NewsEntity newsEntity);

    ArrayList<Object> getNewsSlide();

    NewsDTO getNewsDetailById(Integer id);
}
