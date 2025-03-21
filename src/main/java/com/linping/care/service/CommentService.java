package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.CommentEntity;

public interface CommentService extends MPJBaseService<CommentEntity> {
    boolean insertComment(Integer userId, Integer ordersId, Integer goodsId, String content, Integer grade);
}
