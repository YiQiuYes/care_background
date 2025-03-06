package com.linping.care.controller;

import com.linping.care.dto.UserDTO;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.entity.User;
import com.linping.care.service.UserService;
import com.linping.care.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag(name = "用户控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user/login")
    @Operation(summary = "用户登录")
    public ResultData<HashMap<String, Object>> login(@RequestBody UserDTO userDTO) {
        User user;
        try {
            user = userService.login(userDTO.getPhone(), userDTO.getPassword());
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        // 生成token
        String token = JWTUtils.reNewToken(user.getRefreshToken());
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", user.getRefreshToken());
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
            data.put("token", JWTUtils.reNewToken(newRefreshToken));
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

        User user = new User();
        user.setUsername("用户" + phone.substring(0, 3) + "***" + phone.substring(phone.length() - 5, phone.length() - 1));
        user.setPhone(userDTO.getPhone());
        user.setPassword(userDTO.getPassword());

        try {
            user = userService.register(user);
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        // 生成token
        String token = JWTUtils.reNewToken(user.getRefreshToken());
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", user.getRefreshToken());
        return ResultData.success(data);
    }
}
