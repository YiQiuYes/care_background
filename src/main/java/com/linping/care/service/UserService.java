package com.linping.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linping.care.entity.User;

public interface UserService extends IService<User> {
    User login(String phone, String password);

    String refreshToken(String refreshToken);

    User register(User user);
}
