package com.linping.care.controller;

import com.linping.care.entity.*;
import com.linping.care.service.EmployeeService;
import com.linping.care.service.ImageService;
import com.linping.care.service.UserService;
import com.linping.care.utils.AuthUtil;
import com.linping.care.utils.FileUtil;
import com.linping.care.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

@Tag(name = "员工控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class EmployeeController {
    private final String currentPath = System.getProperty("user.dir");

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.employee-path}")
    private String employeePath;

    @Value("${server.port}")
    private int ip_port;

    @Value("${ip}")
    private String ip;

    private final EmployeeService employeeService;

    private final ImageService imageService;

    private final UserService userService;

    @Operation(summary = "插入员工信息")
    @PostMapping("/employee/insert")
    @Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, schema = @Schema(name = "file", format = "binary"))
    public ResultData<String> insertEmployee(@Schema(name = "name", description = "员工姓名") @RequestParam("name") String name,
                                             @Schema(name = "phone", description = "员工手机号") @RequestParam("phone") String phone,
                                             @Schema(name = "meta", description = "员工介绍标签") @RequestParam("meta") String meta,
                                             @Schema(name = "description", description = "员工介绍") @RequestParam("description") String description,
                                             @Schema(name = "jobNumber", description = "员工号") @RequestParam(value = "jobNumber") BigInteger jobNumber,
                                             @Schema(name = "nursingId", description = "养老院ID") @RequestParam(value = "nursingId") Integer nursingId,
                                             @RequestHeader("token") String token,
                                             @RequestPart(name = "file", value = "file") MultipartFile file) {
        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setName(name);
        employeeEntity.setPhone(phone);
        employeeEntity.setMeta(meta);
        employeeEntity.setDescription(description);
        employeeEntity.setJobNumber(jobNumber);
        employeeEntity.setNursingId(nursingId);
        employeeEntity.setStatus(0);

        boolean save = employeeService.save(employeeEntity);
        if (!save) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "插入员工信息失败");
        }

        ImageEntity imageEntity = new ImageEntity();
        String fileUrl;
        try {
            fileUrl = FileUtil.getImageUrl("employee", file, currentPath, picturePath, picturePath_mapping, employeePath, String.valueOf(ip_port), ip);
        } catch (IOException e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }
        imageEntity.setSrc(fileUrl);
        imageEntity.setEmployeeId(employeeEntity.getId());
        save = imageService.save(imageEntity);
        if (!save) {
            // 删除图片
            FileUtil.deleteImage(fileUrl, currentPath, picturePath, employeePath);
            employeeService.removeById(employeeEntity.getId());
            return ResultData.fail(ReturnCode.RC500.getCode(), "添加员工图片失败");
        }
        return ResultData.success("插入员工信息成功");
    }

    @Operation(summary = "通过id删除床位信息")
    @GetMapping("/employee/delete")
    public ResultData<String> employeeDelete(
            @Schema(name = "id", description = "员工id") @RequestParam("id") Integer id,
            @RequestHeader("token") String token) {
        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        EmployeeEntity employeeEntity = employeeService.getById(id);
        if (employeeEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "员工不存在");
        }

        ImageEntity imageEntity = imageService.getByEmployeeId(employeeEntity.getId());
        if (imageEntity != null) {
            // 删除图片
            FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, employeePath);
            imageService.removeById(imageEntity.getId());

            boolean remove = employeeService.removeById(employeeEntity);
            if (!remove) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "删除员工信息失败");
            }
        }
        return ResultData.success("删除员工信息成功");
    }

    @Operation(summary = "更新员工信息")
    @PostMapping("/employee/update")
    public ResultData<String> employeeUpdate(
            @Schema(name = "id", description = "员工id") @RequestParam("id") Integer id,
            @Schema(name = "name", description = "员工姓名") @RequestParam("name") String name,
            @Schema(name = "phone", description = "员工手机号") @RequestParam("phone") String phone,
            @Schema(name = "meta", description = "员工介绍标签") @RequestParam("meta") String meta,
            @Schema(name = "description", description = "员工介绍") @RequestParam("description") String description,
            @Schema(name = "jobNumber", description = "员工号") @RequestParam(value = "jobNumber") BigInteger jobNumber,
            @Schema(name = "nursingId", description = "养老院ID") @RequestParam(value = "nursingId") Integer nursingId,
            @RequestHeader("token") String token,
            @RequestPart(name = "file", value = "file", required = false) MultipartFile file) {
        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        EmployeeEntity employeeEntity = employeeService.getById(id);
        if (employeeEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "员工不存在");
        }

        employeeEntity.setName(name);
        employeeEntity.setPhone(phone);
        employeeEntity.setMeta(meta);
        employeeEntity.setDescription(description);
        employeeEntity.setJobNumber(jobNumber);
        employeeEntity.setNursingId(nursingId);

        ImageEntity imageEntity = imageService.getByEmployeeId(id);
        if (imageEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "图片不存在");
        }

        if (file != null) {
            String fileUrl;
            try {
                fileUrl = FileUtil.getImageUrl("employee", file, currentPath, picturePath, picturePath_mapping, employeePath, String.valueOf(ip_port), ip);
            } catch (IOException e) {
                return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
            }
            // 删除原图片url
            FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, employeePath);
            imageEntity.setSrc(fileUrl);
            boolean isUpdateImage = imageService.updateById(imageEntity);
            if (!isUpdateImage) {
                FileUtil.deleteImage(fileUrl, currentPath, picturePath, employeePath);
                return ResultData.fail(ReturnCode.RC500.getCode(), "图片更新失败");
            }
        }

        boolean isUpdate = employeeService.updateById(employeeEntity);
        if (isUpdate) {
            return ResultData.success("更新成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "更新失败");
        }
    }

    @Operation(summary = "获取员工信息列表")
    @Parameters({
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true)
    })
    @GetMapping("/employee/list")
    public ResultData<Object> employeeList(@RequestParam(value = "pageNow", defaultValue = "1") int pageNow,
                                           @RequestParam(value = "pageSize", defaultValue = "30") int pageSize,
                                           @RequestHeader("token") String token) {
        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "页码或页数错误");
        }

        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        // 获取用户entity
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }
        HashMap<String, Object> employeeList = employeeService.getEmployeeList(pageNow, pageSize, userEntity.getOwnNursingId());
        if (employeeList == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取员工信息列表失败");
        }

        return ResultData.success(employeeList);
    }

    @Operation(summary = "员工预定")
    @Parameters({
            @Parameter(name = "employeeId", description = "员工id", required = true),
    })
    @GetMapping("/employee/booking")
    public ResultData<String> employeeBooking(
            @Parameter(name = "employeeId", description = "员工id") @RequestParam("employeeId") Integer employeeId,
            @RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);

        EmployeeEntity employeeEntity = employeeService.getById(employeeId);
        if (employeeEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "员工不存在");
        }

        if (employeeEntity.getStatus() != 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "员工已被预定");
        }

        employeeEntity.setStatus(1);
        employeeEntity.setUserId(userEntity.getId());
        boolean update = employeeService.updateById(employeeEntity);
        if (!update) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "预定失败");
        }
        return ResultData.success("预定成功");
    }

    @Operation(summary = "取消预定员工")
    @GetMapping("/employee/cancelBooking")
    public ResultData<String> cancelBooking(@RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        boolean isSuccess = employeeService.cancelBookingEmployeeByUserId(userEntity.getId());
        if (!isSuccess) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "取消预定失败");
        }

        return ResultData.success("取消预定成功");
    }
}
