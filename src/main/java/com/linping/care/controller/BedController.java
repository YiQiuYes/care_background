package com.linping.care.controller;

import com.linping.care.dto.BedDTO;
import com.linping.care.entity.*;
import com.linping.care.service.BedService;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Tag(name = "床位控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class BedController {
    private final String currentPath = System.getProperty("user.dir");

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.bed-path}")
    private String bedPath;

    @Value("${server.port}")
    private int ip_port;

    @Value("${ip}")
    private String ip;

    private final BedService bedService;

    private final UserService userService;

    private final ImageService imageService;

    @Operation(summary = "插入床位信息")
    @PostMapping("/bed/insert")
    @Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, schema = @Schema(name = "file", format = "binary"))
    public ResultData<String> bedInsert(
            @Schema(name = "nursingId", description = "养老院id") @RequestParam("nursingId") Integer nursingId,
            @Schema(name = "size", description = "床位面积") @RequestParam("size") Integer size,
            @Schema(name = "address", description = "地址") @RequestParam("address") String address,
            @Schema(name = "status", description = "预定状态") @RequestParam(value = "status", required = false) Integer status,
            @Schema(name = "meta", description = "标签内容") @RequestParam("meta") String meta,
            @Schema(name = "price", description = "价格") @RequestParam("price") Double price,
            @Schema(name = "description", description = "床位描述") @RequestParam("description") String description,
            @Schema(name = "aptitude", description = "资质等级") @RequestParam(value = "aptitude", required = false) Integer aptitude,
            @RequestHeader("token") String token,
            @RequestPart(name = "file", value = "file") MultipartFile file) {
        // 验证参数是否为空
        if (nursingId == null || size == null || address == null || meta == null || price == null || description == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数不能为空");
        }

        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        BedEntity bedEntity = new BedEntity();
        bedEntity.setNursingId(nursingId);
        bedEntity.setSize(size);
        bedEntity.setAddress(address);
        if (status != null) {
            bedEntity.setStatus(status);
        }
        bedEntity.setMeta(meta);
        bedEntity.setPrice(BigDecimal.valueOf(price));
        bedEntity.setDescription(description);
        if (aptitude != null) {
            bedEntity.setAptitude(aptitude);
        }


        boolean save = bedService.save(bedEntity);
        if (!save) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "添加床位失败");
        }

        ImageEntity imageEntity = new ImageEntity();
        String fileUrl;
        try {
            fileUrl = FileUtil.getImageUrl("bed", file, currentPath, picturePath, picturePath_mapping, bedPath, String.valueOf(ip_port), ip);
        } catch (IOException e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }
        imageEntity.setSrc(fileUrl);
        imageEntity.setBedId(bedEntity.getId());
        save = imageService.save(imageEntity);
        if (!save) {
            // 删除图片
            FileUtil.deleteImage(fileUrl, currentPath, picturePath, bedPath);
            bedService.removeById(bedEntity.getId());
            return ResultData.fail(ReturnCode.RC500.getCode(), "添加床位图片失败");
        }
        return ResultData.success("插入床位信息成功");
    }

    @Operation(summary = "通过id删除床位信息")
    @GetMapping("/bed/delete")
    public ResultData<String> bedDelete(
            @Schema(name = "id", description = "床位id") @RequestParam("id") Integer id,
            @RequestHeader("token") String token) {
        // 验证参数是否为空
        if (id == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数不能为空");
        }

        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        BedEntity bedEntity = bedService.getById(id);
        if (bedEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "床位不存在");
        }

        ImageEntity imageEntity = imageService.getByBedId(bedEntity.getId());
        if (imageEntity != null) {
            // 删除图片
            FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, bedPath);
            imageService.removeById(imageEntity.getId());

            boolean remove = bedService.removeById(bedEntity);
            if (!remove) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "删除床位失败");
            }
        }
        return ResultData.success("删除床位成功");
    }

    @Operation(summary = "更新床位信息")
    @PostMapping("/bed/update")
    public ResultData<String> bedUpdate(
            @Schema(name = "id", description = "床位id") @RequestParam("id") Integer id,
            @Schema(name = "nursingId", description = "养老院id") @RequestParam("nursingId") Integer nursingId,
            @Schema(name = "size", description = "床位面积") @RequestParam("size") Integer size,
            @Schema(name = "address", description = "地址") @RequestParam("address") String address,
            @Schema(name = "status", description = "预定状态") @RequestParam(value = "status", required = false) Integer status,
            @Schema(name = "meta", description = "标签内容") @RequestParam("meta") String meta,
            @Schema(name = "price", description = "价格") @RequestParam("price") Double price,
            @Schema(name = "description", description = "床位描述") @RequestParam("description") String description,
            @Schema(name = "aptitude", description = "资质等级") @RequestParam(value = "aptitude", required = false) Integer aptitude,
            @RequestHeader("token") String token,
            @RequestPart(name = "file", value = "file", required = false) MultipartFile file) {
        // 验证参数是否为空
        if (id == null || nursingId == null || size == null || address == null || meta == null || price == null || description == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数不能为空");
        }

        if (AuthUtil.isAuth(token, userService) && AuthUtil.isNursingAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        BedEntity bedEntity = bedService.getById(id);
        if (bedEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "床位不存在");
        }

        bedEntity.setNursingId(nursingId);
        bedEntity.setSize(size);
        bedEntity.setAddress(address);
        if (status != null && status == 0) {
            bedEntity.setStatus(status);
            bedEntity.setOwnId(null);
        }
        bedEntity.setMeta(meta);
        bedEntity.setPrice(BigDecimal.valueOf(price));
        bedEntity.setDescription(description);
        if (aptitude != null) {
            bedEntity.setAptitude(aptitude);
        }

        ImageEntity imageEntity = imageService.getByBedId(id);
        if (imageEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "图片不存在");
        }

        if (file != null) {
            String fileUrl;
            try {
                fileUrl = FileUtil.getImageUrl("bed", file, currentPath, picturePath, picturePath_mapping, bedPath, String.valueOf(ip_port), ip);
            } catch (IOException e) {
                return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
            }
            // 删除原图片url
            FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, bedPath);
            imageEntity.setSrc(fileUrl);
            boolean isUpdateImage = imageService.updateById(imageEntity);
            if (!isUpdateImage) {
                FileUtil.deleteImage(fileUrl, currentPath, picturePath, bedPath);
                return ResultData.fail(ReturnCode.RC500.getCode(), "图片更新失败");
            }
        }

        boolean isUpdate = bedService.updateById(bedEntity);
        if (isUpdate) {
            return ResultData.success("更新成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "更新失败");
        }
    }

    @Operation(summary = "获取床位信息列表")
    @Parameters({
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true)
    })
    @GetMapping("/bed/list")
    public ResultData<Object> bedList(@RequestParam(value = "pageNow", defaultValue = "1") int pageNow,
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
        HashMap<String, Object> bedList = bedService.getBedList(pageNow, pageSize, userEntity.getOwnNursingId());
        if (bedList == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取床位信息列表失败");
        }

        return ResultData.success(bedList);
    }

    @Operation(summary = "床位预定")
    @Parameters({
            @Parameter(name = "bedId", description = "床位id", required = true),
    })
    @GetMapping("/bed/booking")
    public ResultData<String> bedBooking(
            @Parameter(name = "bedId", description = "床位id") @RequestParam("bedId") Integer bedId,
            @RequestHeader("token") String token) {
        // 验证参数是否为空
        if (bedId == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数不能为空");
        }

        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);

        // 查看是否已经预定床位
        boolean isTrue = bedService.isAlreadyBooking(userEntity.getId());
        if (isTrue) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "已预定床位");
        }

        BedEntity bedEntity = bedService.getById(bedId);
        if (bedEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "床位不存在");
        }

        if (bedEntity.getStatus() != 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "床位已被预定");
        }

        bedEntity.setStatus(1);
        bedEntity.setOwnId(userEntity.getId());
        boolean update = bedService.updateById(bedEntity);
        if (!update) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "预定失败");
        }
        return ResultData.success("预定成功");
    }

    @Operation(summary = "查询预定的床位信息")
    @GetMapping("/bed/bookingBedInfo")
    public ResultData<Object> bookingBedInfo(@RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        List<BedDTO> bedDTOs = bedService.getBedByUserId(userEntity.getId());
        return ResultData.success(bedDTOs);
    }

    @Operation(summary = "取消预定床位")
    @GetMapping("/bed/cancelBooking")
    public ResultData<String> cancelBooking(@RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        boolean isSuccess = bedService.cancelBookingBedByOwnId(userEntity.getId());
        if (!isSuccess) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "取消预定失败");
        }

        return ResultData.success("取消预定成功");
    }
}
