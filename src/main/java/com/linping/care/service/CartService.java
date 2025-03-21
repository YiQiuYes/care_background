package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.entity.CartEntity;

import java.util.HashMap;

public interface CartService extends MPJBaseService<CartEntity> {
    HashMap<String, Object> getCartList(Integer userId, int pageNow, int pageSize);
}
