package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.UserInfoDTO;
import com.linping.care.entity.UserEntity;

import java.util.HashMap;

public interface UserService extends MPJBaseService<UserEntity> {
    UserEntity login(String phone, String password);

    String refreshToken(String refreshToken);

    UserEntity register(UserEntity userEntity);

    UserInfoDTO getUserInfo(String token);

    HashMap<String, Object> getUserList(Integer pageNow, Integer pageSize, Integer ownNursingId);

    UserInfoDTO getUserByPhone(String phone);
}
