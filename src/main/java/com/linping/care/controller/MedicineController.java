package com.linping.care.controller;

import com.linping.care.entity.MedicineEntity;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.entity.UserEntity;
import com.linping.care.service.MedicineService;
import com.linping.care.service.UserService;
import com.linping.care.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.HashMap;

@Tag(name = "药物管理控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;

    private final UserService userService;

    @Operation(summary = "插入用药信息")
    @PostMapping("/medicine/insert")
    @Parameters({
            @Parameter(name = "name", description = "药物名称", required = true),
            @Parameter(name = "dosage", description = "药剂量", required = true),
            @Parameter(name = "startTime", description = "开始时间", required = true),
            @Parameter(name = "endTime", description = "结束时间", required = true),
            @Parameter(name = "employeeId", description = "雇员id", required = true)

    })
    public ResultData<Object> medicineInsert(@RequestParam("name") String name,
                                             @RequestParam("dosage") String dosage,
                                             @RequestParam("startTime") Date startTime,
                                             @RequestParam("endTime") Date endTime,
                                             @RequestParam("employeeId") Integer employeeId) {
        MedicineEntity medicineEntity = new MedicineEntity();
        medicineEntity.setName(name);
        medicineEntity.setDosage(dosage);
        medicineEntity.setStartTime(startTime);
        medicineEntity.setEndTime(endTime);
        medicineEntity.setEmployeeId(employeeId);

        boolean result = medicineService.save(medicineEntity);
        if (result) {
            return ResultData.success("用药信息插入成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用药信息插入失败");
        }
    }

    @Operation(summary = "删除用药信息")
    @PostMapping("/medicine/delete")
    @Parameters({
            @Parameter(name = "id", description = "药物id", required = true)
    })
    public ResultData<Object> medicineDelete(@RequestParam("id") Integer id) {
        boolean result = medicineService.removeById(id);
        if (result) {
            return ResultData.success("用药信息删除成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用药信息删除失败");
        }
    }

    @Operation(summary = "更新用药信息")
    @PostMapping("/medicine/update")
    @Parameters({
            @Parameter(name = "id", description = "药物id", required = true),
            @Parameter(name = "name", description = "药物名称", required = true),
            @Parameter(name = "dosage", description = "药剂量", required = true),
            @Parameter(name = "startTime", description = "开始时间", required = true),
            @Parameter(name = "endTime", description = "结束时间", required = true),
            @Parameter(name = "employeeId", description = "雇员id", required = true)
    })
    public ResultData<Object> medicineUpdate(@RequestParam("id") Integer id,
                                             @RequestParam("name") String name,
                                             @RequestParam("dosage") String dosage,
                                             @RequestParam("startTime") Date startTime,
                                             @RequestParam("endTime") Date endTime,
                                             @RequestParam("employeeId") Integer employeeId) {
        MedicineEntity medicineEntity = medicineService.getById(id);
        if (medicineEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用药信息不存在");
        }
        medicineEntity.setName(name);
        medicineEntity.setDosage(dosage);
        medicineEntity.setStartTime(startTime);
        medicineEntity.setEndTime(endTime);
        medicineEntity.setEmployeeId(employeeId);

        boolean result = medicineService.updateById(medicineEntity);
        if (result) {
            return ResultData.success("用药信息更新成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用药信息更新失败");
        }
    }

    @Operation(summary = "查询用药信息列表")
    @PostMapping("/medicine/list")
    @Parameters({
            @Parameter(name = "pageNow", description = "当前页面", required = true),
            @Parameter(name = "pageSize", description = "每页数量", required = true)
    })
    public ResultData<Object> medicineList(@RequestParam(value = "pageNow", defaultValue = "1") Integer pageNow,
                                           @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
                                           @RequestHeader("token") String token) {
        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "页码或页数错误");
        }

        // 获取用户entity
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        HashMap<String, Object> medicineList = medicineService.getMedicineList(pageNow, pageSize);
        if (medicineList == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取用药信息列表失败");
        }

        return ResultData.success(medicineList);
    }
}
