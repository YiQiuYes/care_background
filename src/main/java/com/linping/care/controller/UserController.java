package com.linping.care.controller;

import com.linping.care.dto.UserDTO;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.entity.UserEntity;
import com.linping.care.service.UserService;
import com.linping.care.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag(name = "用户控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.avatar-path}")
    private String avatarPath;

    @Value("${server.port}")
    private int ip_port;

    private final UserService userService;

    @PostMapping("/user/login")
    @Operation(summary = "用户登录")
    public ResultData<HashMap<String, Object>> login(@RequestBody UserDTO userDTO) {
        UserEntity userEntity;
        try {
            userEntity = userService.login(userDTO.getPhone(), userDTO.getPassword());
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        // 生成token
        String token = JWTUtil.reNewToken(userEntity.getRefreshToken());
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", userEntity.getRefreshToken());
        return ResultData.success(data);
    }

    @GetMapping("/user/refreshToken")
    @Operation(summary = "刷新refreshToken")
    @Parameters({
            @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    })
    public ResultData<HashMap<String, Object>> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            String newRefreshToken = userService.refreshToken(refreshToken);
            HashMap<String, Object> data = new HashMap<>();
            data.put("refreshToken", newRefreshToken);
            data.put("token", JWTUtil.reNewToken(newRefreshToken));
            return ResultData.success(data);
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }
    }

    @PostMapping("/user/register")
    @Operation(summary = "用户注册")
    public ResultData<HashMap<String, Object>> register(@RequestBody UserDTO userDTO) {
        String phone = userDTO.getPhone();
        // 验证手机号是否合法
        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.matches()) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "手机号不合法");
        }

        if (userDTO.getPassword().isEmpty()) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "密码不能为空");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("用户" + phone.substring(0, 3) + "***" + phone.substring(phone.length() - 5, phone.length() - 1));
        userEntity.setPhone(userDTO.getPhone());
        userEntity.setPassword(userDTO.getPassword());
        userEntity.setAuth(1);
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取ip失败");
        }
        String avatar_src = "http://" + ip + ":" + ip_port + picturePath_mapping + avatarPath + "default_avatar.png";
        userEntity.setAvatar(avatar_src);
        userEntity.setIntroduction("这个人很懒，什么都没有留下~");

        try {
            userEntity = userService.register(userEntity);
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        // 生成token
        String token = JWTUtil.reNewToken(userEntity.getRefreshToken());
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", userEntity.getRefreshToken());
        return ResultData.success(data);
    }

    @GetMapping("/user/getUserInfo")
    @Operation(summary = "获取用户信息")
    public ResultData<UserEntity> getUserInfo(@RequestHeader("token") String token) {
        UserEntity userEntity = userService.getUserInfo(token);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }
        return ResultData.success(userEntity);
    }
}
