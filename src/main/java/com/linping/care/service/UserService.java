package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.UserInfoDTO;
import com.linping.care.entity.UserEntity;

public interface UserService extends MPJBaseService<UserEntity> {
    UserEntity login(String phone, String password);

    String refreshToken(String refreshToken);

    UserEntity register(UserEntity userEntity);

    UserInfoDTO getUserInfo(String token);
}
