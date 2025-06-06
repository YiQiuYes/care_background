package com.linping.care.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.DeleteJoinWrapper;
import com.linping.care.dto.AddressDTO;
import com.linping.care.dto.UserDTO;
import com.linping.care.dto.UserInfoDTO;
import com.linping.care.entity.*;
import com.linping.care.service.AddressService;
import com.linping.care.service.ImageService;
import com.linping.care.service.NursingService;
import com.linping.care.service.UserService;
import com.linping.care.utils.AuthUtil;
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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

    @Value("${ip}")
    private String ip;

    private final UserService userService;

    private final ImageService imageService;

    private final AddressService addressService;

    private final NursingService nursingService;

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
    @Parameters({@Parameter(name = "refreshToken", description = "刷新令牌", required = true)})
    public ResultData<HashMap<String, Object>> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            String newRefreshToken = userService.refreshToken(refreshToken);
            HashMap<String, Object> data = new HashMap<>();
            data.put("refreshToken", newRefreshToken);
            data.put("token", JWTUtil.reNewToken(newRefreshToken));
            return ResultData.success(data);
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC402.getCode(), e.getMessage());
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
        userEntity.setUsername("用户" + phone.substring(0, 3) + "***" + phone.substring(phone.length() - 4));
        userEntity.setPhone(userDTO.getPhone());
        userEntity.setPassword(userDTO.getPassword());
        userEntity.setAuth(1);
        userEntity.setIntroduction("这个人很懒，什么都没有留下~");

        try {
            userEntity = userService.register(userEntity);
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        ImageEntity imageEntity = new ImageEntity();
        try {
            ClassPathResource classPathResource = new ClassPathResource("default_avatar.png");
            InputStream is = classPathResource.getInputStream();

            String avatar_src = FileUtil.getImageUrl("avatar", "default_avatar.png", is, currentPath, picturePath, picturePath_mapping, avatarPath, String.valueOf(ip_port), ip);
            imageEntity.setSrc(avatar_src);
            imageEntity.setUserId(userEntity.getId());
            boolean save = imageService.save(imageEntity);
            if (!save) {
                FileUtil.deleteImage(avatar_src, currentPath, picturePath, avatarPath);
                return ResultData.fail(ReturnCode.RC500.getCode(), "头像保存失败");
            }
        } catch (Exception e) {
            userService.removeById(userEntity);
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        // 生成token
        String token = JWTUtil.reNewToken(userEntity.getRefreshToken());
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", userEntity.getRefreshToken());
        return ResultData.success(data);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/user/getUserInfo")
    public ResultData<UserInfoDTO> getUserInfo(@RequestHeader("token") String token) {
        UserInfoDTO userInfoDTO = userService.getUserInfo(token);
        if (userInfoDTO == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        return ResultData.success(userInfoDTO);
    }

    @Operation(summary = "添加用户地址")
    @PostMapping("/user/addAddress")
    public ResultData<String> addAddress(@RequestBody AddressDTO addressDTO, @RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        // 验证参数
        if (addressDTO.getName().isEmpty() || addressDTO.getPhone().isEmpty() || addressDTO.getProvince().isEmpty() || addressDTO.getCity().isEmpty() || addressDTO.getDistrict().isEmpty() || addressDTO.getDetail().isEmpty() || addressDTO.getIsDefault() == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setUserId(Integer.valueOf(userId));
        addressEntity.setName(addressDTO.getName());
        addressEntity.setPhone(addressDTO.getPhone());
        addressEntity.setProvince(addressDTO.getProvince());
        addressEntity.setCity(addressDTO.getCity());
        addressEntity.setDistrict(addressDTO.getDistrict());
        addressEntity.setDetail(addressDTO.getDetail());
        addressEntity.setIsDefault(addressDTO.getIsDefault());

        if (addressDTO.getIsDefault()) {
            UpdateWrapper<AddressEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", userId);
            updateWrapper.set("default", false);
            addressService.update(updateWrapper);
        }

        boolean save = addressService.save(addressEntity);
        if (!save) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "地址添加失败");
        }

        return ResultData.success("添加成功");
    }

    @Operation(summary = "更新用户地址")
    @PostMapping("/user/updateAddress")
    public ResultData<String> updateAddress(@RequestBody AddressDTO addressDTO, @RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        // 验证参数
        if (addressDTO.getId() == null || addressDTO.getName().isEmpty() || addressDTO.getPhone().isEmpty() || addressDTO.getProvince().isEmpty() || addressDTO.getCity().isEmpty() || addressDTO.getDistrict().isEmpty() || addressDTO.getDetail().isEmpty() || addressDTO.getIsDefault() == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        AddressEntity addressEntity = addressService.getById(addressDTO.getId());
        if (addressEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "地址不存在");
        }

        addressEntity.setName(addressDTO.getName());
        addressEntity.setPhone(addressDTO.getPhone());
        addressEntity.setProvince(addressDTO.getProvince());
        addressEntity.setCity(addressDTO.getCity());
        addressEntity.setDistrict(addressDTO.getDistrict());
        addressEntity.setDetail(addressDTO.getDetail());
        addressEntity.setIsDefault(addressDTO.getIsDefault());

        if (addressDTO.getIsDefault()) {
            UpdateWrapper<AddressEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", userId);
            updateWrapper.set("is_default", false);
            addressService.update(updateWrapper);
        }

        boolean update = addressService.updateById(addressEntity);
        if (!update) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "地址更新失败");
        }

        return ResultData.success("更新成功");
    }

    @Operation(summary = "获取用户地址列表")
    @GetMapping("/user/addressList")
    public ResultData<Object> addressList(@RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        List<AddressDTO> addressDTOS = addressService.addressList(Integer.valueOf(userId));
        return ResultData.success(addressDTOS);
    }

    @Operation(summary = "获取用户默认地址")
    @GetMapping("/user/defaultAddress")
    public ResultData<Object> defaultAddress(@RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        AddressEntity addressEntity = addressService.defaultAddress(Integer.valueOf(userId));
        if (addressEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "地址不存在");
        }
        return ResultData.success(addressEntity);
    }

    @Operation(summary = "替换图片ip地址")
    @GetMapping("/user/replaceIp")
    public ResultData<String> replaceIp(@RequestHeader("token") String token) {
        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        imageService.replaceIp(ip, String.valueOf(ip_port));

        return ResultData.success("替换成功");
    }

    @Operation(summary = "删除用户")
    @Parameters({@Parameter(name = "deleteId", description = "需要删除的用户id", required = true)})
    @GetMapping("/user/deleteUser")
    public ResultData<String> deleteUser(@RequestHeader("token") String token, @RequestParam("deleteId") Integer deleteId) {
        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        ImageEntity imageEntity = imageService.userImageById(deleteId);

        DeleteJoinWrapper<ImageEntity> deleteJoinWrapper = JoinWrappers.delete(ImageEntity.class).deleteAll().leftJoin(UserEntity.class, UserEntity::getId, ImageEntity::getUserId).eq(ImageEntity::getUserId, deleteId);

        boolean isDelete = imageService.deleteJoin(deleteJoinWrapper);
        if (!isDelete) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "删除用户失败");
        }

        FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, avatarPath);
        return ResultData.success("删除成功");
    }

    @Operation(summary = "设置养老院管理员")
    @GetMapping("/user/setNursingAdmin")
    @Parameters({
            @Parameter(name = "userId", description = "养老院管理员id", required = true),
            @Parameter(name = "nursingId", description = "养老院id", required = true),
            @Parameter(name = "role", description = "权限等级", required = true)

    })
    public ResultData<String> setNursingAdmin(@RequestParam(value = "userId") Integer userId,
                                              @RequestParam(value = "nursingId") Integer nursingId,
                                              @RequestParam(value = "role") Integer role,
                                              @RequestHeader("token") String token) {
        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        NursingEntity nursingEntity = nursingService.getById(nursingId);
        if (nursingEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "养老院不存在");
        }

        if (role <= 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限等级不合法");
        }

        userEntity.setNursingRole(role);
        userEntity.setOwnNursingId(nursingId);
        if (role == 1) {
            userEntity.setOwnNursingId(null);
        }
        boolean update = userService.updateById(userEntity);
        if (!update) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "添加养老院管理员失败");
        }

        return ResultData.success("添加成功");
    }

    @Operation(summary = "获取管理员用户列表")
    @GetMapping("/user/userList")
    @Parameters({
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true)
    })
    public ResultData<Object> userList(@RequestHeader("token") String token,
                                       @RequestParam(value = "pageNow") Integer pageNow,
                                       @RequestParam(value = "pageSize") Integer pageSize) {
        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "页码或页数错误");
        }

        // 获取用户entity
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        HashMap<String, Object> bedList = userService.getUserList(pageNow, pageSize, userEntity.getOwnNursingId());
        if (bedList == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取床位信息列表失败");
        }

        return ResultData.success(bedList);
    }

    @Operation(summary = "根据手机号获取用户信息")
    @GetMapping("/user/getUserByPhone")
    @Parameters({
            @Parameter(name = "phone", description = "手机号", required = true)
    })
    public ResultData<Object> getUserByPhone(@RequestParam(value = "phone") String phone,
                                             @RequestHeader("token") String token) {
        if (phone.isEmpty()) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "手机号不能为空");
        }

        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        UserInfoDTO userInfoDTO = userService.getUserByPhone(phone);
        if (userInfoDTO == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        return ResultData.success(userInfoDTO);
    }
}
