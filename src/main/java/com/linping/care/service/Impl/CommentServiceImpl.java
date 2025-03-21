package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.linping.care.entity.CommentEntity;
import com.linping.care.entity.OrdersEntity;
import com.linping.care.mapper.CommentMapper;
import com.linping.care.service.CommentService;
import com.linping.care.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl extends MPJBaseServiceImpl<CommentMapper, CommentEntity> implements CommentService {
    private final CommentMapper commentMapper;
    private final OrdersService ordersService;

    @Override
    public boolean insertComment(Integer userId, Integer ordersId, Integer goodsId, String content, Integer grade) {
        // 查询订单是否存在
        OrdersEntity orders = ordersService.getById(ordersId);
        if (orders == null) {
            return false;
        }

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUserId(userId);
        commentEntity.setGoodsId(goodsId);
        commentEntity.setContent(content);
        commentEntity.setGrade(grade);
        commentEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        boolean isSuccess = commentMapper.insert(commentEntity) > 0;
        if (isSuccess) {
            // 更新订单状态
            return ordersService.modifyStatusById(ordersId, 4);
        }
        commentMapper.deleteById(commentEntity);
        return false;
    }
}
