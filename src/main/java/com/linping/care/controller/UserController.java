package com.linping.care.controller;

import com.linping.care.dto.UserDTO;
import com.linping.care.dto.UserInfoDTO;
import com.linping.care.entity.ImageEntity;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.entity.UserEntity;
import com.linping.care.service.ImageService;
import com.linping.care.service.UserService;
import com.linping.care.utils.FileUtil;
import com.linping.care.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag(name = "用户控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final String currentPath = System.getProperty("user.dir");

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.avatar-path}")
    private String avatarPath;

    @Value("${server.port}")
    private int ip_port;

    private final UserService userService;

    private final ImageService imageService;

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

        ImageEntity imageEntity = new ImageEntity();
        try {
            ClassPathResource classPathResource = new ClassPathResource("default_avatar.png");
            File file = classPathResource.getFile();
            String avatar_src = FileUtil.getImageUrl("avatar", file, currentPath, picturePath, picturePath_mapping, avatarPath, String.valueOf(ip_port));
            imageEntity.setSrc(avatar_src);
            boolean save = imageService.save(imageEntity);
            if (!save) {
                FileUtil.deleteImage(avatar_src, currentPath, picturePath, avatarPath);
                return ResultData.fail(ReturnCode.RC500.getCode(), "头像保存失败");
            }
            userEntity.setAvatarImageId(imageEntity.getId());
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取ip失败");
        }

        userEntity.setIntroduction("这个人很懒，什么都没有留下~");
        try {
            userEntity = userService.register(userEntity);
        } catch (Exception e) {
            FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, avatarPath);
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
    public ResultData<UserInfoDTO> getUserInfo(@RequestHeader("token") String token) {
        UserEntity userEntity = userService.getUserInfo(token);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userEntity.getId());
        userInfoDTO.setUsername(userEntity.getUsername());
        userInfoDTO.setIntroduction(userEntity.getIntroduction());
        userInfoDTO.setAuth(userEntity.getAuth());

        ImageEntity imageEntity = imageService.getById(userEntity.getAvatarImageId());
        if (imageEntity != null) {
            userInfoDTO.setAvatar(imageEntity.getSrc());
        } else {
           return ResultData.fail(ReturnCode.RC500.getCode(), "头像不存在");
        }
        return ResultData.success(userInfoDTO);
    }
}
