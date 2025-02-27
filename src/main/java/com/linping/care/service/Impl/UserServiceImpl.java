package com.linping.care.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linping.care.mapper.UserMapper;
import com.linping.care.entity.User;
import com.linping.care.service.UserService;
import com.linping.care.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserMapper userMapper;

    @Override
    public User login(String phone, String password) {
        if (phone.isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        } else if (password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new IllegalArgumentException("账号或密码错误");
        }

        HashMap<String, String> payload = new HashMap<>();
        payload.put("id", user.getId().toString());
        String refreshToken = JWTUtils.generaRefreshToken(payload);
        user.setRefreshToken(refreshToken);
        int update = userMapper.updateById(user);
        if (update == 0) {
            throw new IllegalArgumentException("登录失败");
        }
        user.setPassword("");
        return user;
    }

    @Override
    public String refreshToken(String refreshToken) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("refresh_token", refreshToken);
        String newRefreshToken = JWTUtils.reNewRefreshToken(refreshToken);

        updateWrapper.set("refresh_token", newRefreshToken);
        int update = userMapper.update(null, updateWrapper);
        if (update == 0) {
            throw new IllegalArgumentException("刷新令牌错误");
        }
        return newRefreshToken;
    }

    @Override
    public User register(User user) {
        if (user.getPhone().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        } else if (user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", user.getPhone());
        User queryUser = userMapper.selectOne(queryWrapper);
        if (queryUser != null) {
            throw new IllegalArgumentException("手机号已存在");
        }

        int insert = userMapper.insert(user);
        if (insert == 0) {
            throw new IllegalArgumentException("注册失败");
        }

        queryUser = userMapper.selectOne(queryWrapper);
        HashMap<String, String> payload = new HashMap<>();
        payload.put("id", queryUser.getId().toString());
        queryUser.setRefreshToken(JWTUtils.generaRefreshToken(payload));
        queryUser.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("phone", queryUser.getUsername());
        int update = userMapper.updateById(queryUser);
        if (update == 0) {
            throw new IllegalArgumentException("注册失败");
        }

        queryUser.setPassword("");
        return queryUser;
    }
}
