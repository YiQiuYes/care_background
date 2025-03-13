package com.linping.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linping.care.entity.NewsEntity;

import java.util.ArrayList;
import java.util.HashMap;

public interface NewsService extends IService<NewsEntity> {
    HashMap<String, Object> getNewsList(String type, int pageNow, int pageSize);

    boolean insertNews(NewsEntity newsEntity);

    ArrayList<Object> getNewsSlide();
}
