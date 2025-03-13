package com.linping.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linping.care.entity.UserEntity;

public interface UserService extends IService<UserEntity> {
    UserEntity login(String phone, String password);

    String refreshToken(String refreshToken);

    UserEntity register(UserEntity userEntity);

    UserEntity getUserInfo(String token);
}
