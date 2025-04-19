package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.DeleteJoinWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.UserInfoDTO;
import com.linping.care.entity.ImageEntity;
import com.linping.care.entity.UserEntity;
import com.linping.care.mapper.UserMapper;
import com.linping.care.service.UserService;
import com.linping.care.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends MPJBaseServiceImpl<UserMapper, UserEntity> implements UserService {
    private final UserMapper userMapper;

    @Override
    public UserEntity login(String phone, String password) {
        if (phone.isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        } else if (password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);

        UserEntity userEntity = userMapper.selectOne(queryWrapper);
        String passwordMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
        if (userEntity == null || !userEntity.getPassword().equals(passwordMD5)) {
            throw new IllegalArgumentException("账号或密码错误");
        }

        HashMap<String, String> payload = new HashMap<>();
        payload.put("id", userEntity.getId().toString());
        payload.put("type", "refreshToken");
        String refreshToken = JWTUtil.generaRefreshToken(payload);
        userEntity.setRefreshToken(refreshToken);
        int update = userMapper.updateById(userEntity);
        if (update == 0) {
            throw new IllegalArgumentException("登录失败");
        }
        userEntity.setPassword("");
        return userEntity;
    }

    @Override
    public String refreshToken(String refreshToken) {
        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("refresh_token", refreshToken);
        String newRefreshToken = JWTUtil.reNewRefreshToken(refreshToken);

        updateWrapper.set("refresh_token", newRefreshToken);
        int update = userMapper.update(null, updateWrapper);
        if (update == 0) {
            throw new IllegalArgumentException("刷新令牌错误");
        }
        return newRefreshToken;
    }

    @Override
    public UserEntity register(UserEntity userEntity) {
        if (userEntity.getPhone().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        } else if (userEntity.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", userEntity.getPhone());
        UserEntity queryUserEntity = userMapper.selectOne(queryWrapper);
        if (queryUserEntity != null) {
            throw new IllegalArgumentException("手机号已存在");
        }

        userEntity.setNursingRole(1);
        int insert = userMapper.insert(userEntity);
        if (insert == 0) {
            throw new IllegalArgumentException("注册失败");
        }

        queryUserEntity = userMapper.selectOne(queryWrapper);
        HashMap<String, String> payload = new HashMap<>();
        payload.put("id", queryUserEntity.getId().toString());
        payload.put("type", "refreshToken");
        queryUserEntity.setRefreshToken(JWTUtil.generaRefreshToken(payload));
        queryUserEntity.setPassword(DigestUtils.md5DigestAsHex(userEntity.getPassword().getBytes()));

        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("phone", queryUserEntity.getUsername());
        int update = userMapper.updateById(queryUserEntity);
        if (update == 0) {
            throw new IllegalArgumentException("注册失败");
        }

        queryUserEntity.setPassword("");
        return queryUserEntity;
    }

    @Override
    public UserInfoDTO getUserInfo(String token) {
        String id = JWTUtil.getTokenInfo(token).getClaim("id").asString();
        if (id == null) {
            throw new IllegalArgumentException("token无效");
        }

        MPJLambdaWrapper<UserEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(UserEntity.class);
        queryWrapper.selectAs(ImageEntity::getSrc, UserInfoDTO::getAvatar);
        queryWrapper.leftJoin(ImageEntity.class, ImageEntity::getUserId, UserEntity::getId);
        queryWrapper.eq(UserEntity::getId, id);
        UserInfoDTO userInfoDTO = userMapper.selectJoinOne(UserInfoDTO.class, queryWrapper);
        if (userInfoDTO == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        userInfoDTO.setPassword("");
        userInfoDTO.setRefreshToken("");
        userInfoDTO.setPhone("");
        return userInfoDTO;
    }
}
