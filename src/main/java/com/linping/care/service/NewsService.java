package com.linping.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linping.care.entity.News;

import java.util.ArrayList;
import java.util.HashMap;

public interface NewsService extends IService<News> {
    HashMap<String, Object> getNewsList(String type, int pageNow, int pageSize);

    boolean insertNews(News news);

    ArrayList<Object> getNewsSlide();
}
