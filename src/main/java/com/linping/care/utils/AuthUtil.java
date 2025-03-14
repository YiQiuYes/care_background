package com.linping.care.utils;

import com.linping.care.entity.UserEntity;
import com.linping.care.service.UserService;

public class AuthUtil {
    public static boolean isAuth(String token, UserService userService) {
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);

        return userEntity.getAuth() <= 1;
    }
}
