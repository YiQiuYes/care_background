package com.linping.care.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.DeleteJoinWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linping.care.dto.NursingBookingDTO;
import com.linping.care.dto.NursingDTO;
import com.linping.care.entity.*;
import com.linping.care.service.ImageService;
import com.linping.care.service.NursingBookingService;
import com.linping.care.service.NursingService;
import com.linping.care.service.UserService;
import com.linping.care.utils.AuthUtil;
import com.linping.care.utils.CheckParamUtil;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Tag(name = "养老院控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class NursingController {
    private final String currentPath = System.getProperty("user.dir");

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.nursing-path}")
    private String nursingPath;

    @Value("${server.port}")
    private int ip_port;

    private final UserService userService;

    private final NursingService nursingService;

    private final ImageService imageService;

    private final NursingBookingService nursingBookingService;

    @Operation(summary = "获取养老院信息列表")
    @Parameters({
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true)
    })
    @GetMapping("/nursing/list")
    public ResultData<Object> nursingList(@RequestParam(value = "pageNow", defaultValue = "1") int pageNow,
                                          @RequestParam(value = "pageSize", defaultValue = "30") int pageSize) {
        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(400, "养老院获取信息列表api中页码或页数错误");
        }


        MPJLambdaWrapper<NursingEntity> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectFilter(NursingEntity.class, i -> !i.getColumn().equals("content"));

        HashMap<String, Object> nursingMap = new HashMap<>();
        Page<NursingDTO> page = new Page<>(pageNow, pageSize);
        page = nursingService.selectJoinListPage(page, NursingDTO.class, queryWrapper);
        List<NursingDTO> records = page.getRecords();

        MPJLambdaQueryWrapper<ImageEntity> imageQueryWrapper = new MPJLambdaQueryWrapper<>();
        for (NursingDTO nursingDTO : records) {
            imageQueryWrapper.selectAll(ImageEntity.class);
            imageQueryWrapper.eq(ImageEntity::getNursingId, nursingDTO.getId());
            List<ImageEntity> list = imageService.list(imageQueryWrapper);
            if (!list.isEmpty()) {
                nursingDTO.setInfoImage(list.get(0).getSrc());
            }
        }
        nursingMap.put("pages", page.getPages());
        nursingMap.put("total", page.getTotal());
        nursingMap.put("records", records);

        return ResultData.success(nursingMap);
    }

    @Operation(summary = "插入养老院信息")
    @PostMapping("/nursing/insert")
    @Parameter(name = "files", description = "文件", in = ParameterIn.DEFAULT,
            schema = @Schema(name = "files", format = "binary"))
    public ResultData<String> nursingInsert(@Schema(name = "name", description = "名称") @RequestParam("name") String name,
                                            @Schema(name = "address", description = "地址") @RequestParam("address") String address,
                                            @Schema(name = "phone", description = "电话") @RequestParam("phone") String phone,
                                            @Schema(name = "content", description = "介绍") @RequestParam("content") String content,
                                            @Schema(name = "time", description = "营业时间") @RequestParam("time") String time,
                                            @Schema(name = "bunkCount", description = "床位数量") @RequestParam("bunkCount") Integer bunkCount,
                                            @Schema(name = "workerCount", description = "职工数量") @RequestParam("workerCount") Integer workerCount,
                                            @Schema(name = "size", description = "面积大小") @RequestParam("size") Long size,
                                            @Schema(name = "aptitude", description = "资质等级") @RequestParam("aptitude") Integer aptitude,
                                            @Schema(name = "location", description = "经纬度信息") @RequestParam("location") String location,
                                            @RequestHeader("token") String token,
                                            @RequestPart(name = "files", value = "files") MultipartFile[] files) {
        // 验证参数是否为空
        if (name == null || address == null || phone == null || content == null || time == null || bunkCount == null || workerCount == null || size == null || aptitude == null || location == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数不能为空");
        }

        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        NursingEntity nursingEntity = new NursingEntity();
        nursingEntity.setName(name);
        nursingEntity.setAddress(address);
        nursingEntity.setPhone(phone);
        nursingEntity.setContent(content);
        nursingEntity.setTime(time);
        nursingEntity.setBunkCount(bunkCount);
        nursingEntity.setWorkerCount(workerCount);
        nursingEntity.setSize(size);
        nursingEntity.setAptitude(aptitude);
        nursingEntity.setLocation(location);


        boolean save = nursingService.save(nursingEntity);
        if (!save) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "添加养老院失败");
        }

        ArrayList<String> urls;
        try {
            urls = FileUtil.getImageUrl("nursing", files, currentPath, picturePath, picturePath_mapping, nursingPath, String.valueOf(ip_port));
        } catch (IOException e) {
            log.error("图片创建失败, 原因：{}", e.getMessage());
            return ResultData.fail(ReturnCode.RC500.getCode(), "图片创建失败");
        }

        ArrayList<ImageEntity> imageEntities = new ArrayList<>();
        for (String url : urls) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setSrc(url);
            imageEntity.setNursingId(nursingEntity.getId());
            imageEntities.add(imageEntity);
        }
        save = imageService.saveBatch(imageEntities);
        if (!save) {
            for (String url : urls) {
                FileUtil.deleteImage(url, currentPath, picturePath, nursingPath);
            }
            return ResultData.fail(ReturnCode.RC500.getCode(), "养老院图片插入失败");
        }

        return ResultData.success("插入养老院信息成功");
    }

    @Operation(summary = "根据id获取养老院信息")
    @Parameters({
            @Parameter(name = "id", description = "养老院ID", required = true)
    })
    @GetMapping("/nursing/getNursingById")
    public ResultData<NursingDTO> getNursingById(@RequestParam("id") Integer id) {
        if (id == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }
        NursingEntity nursingEntity = nursingService.getById(id);
        if (nursingEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "养老院信息不存在");
        }

        MPJLambdaQueryWrapper<ImageEntity> queryWrapper = new MPJLambdaQueryWrapper<>();
        queryWrapper.selectAll(ImageEntity.class);
        queryWrapper.eq(ImageEntity::getNursingId, id);
        List<ImageEntity> imageEntities = imageService.list(queryWrapper);
        if (imageEntities == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "养老院图片信息不存在");
        }

        List<String> images = new ArrayList<>();
        for (ImageEntity imageEntity : imageEntities) {
            images.add(imageEntity.getSrc());
        }

        NursingDTO nursingDTO = new NursingDTO();
        nursingDTO.setId(nursingEntity.getId());
        nursingDTO.setName(nursingEntity.getName());
        nursingDTO.setAddress(nursingEntity.getAddress());
        nursingDTO.setPhone(nursingEntity.getPhone());
        nursingDTO.setContent(nursingEntity.getContent());
        nursingDTO.setTime(nursingEntity.getTime());
        nursingDTO.setBunkCount(nursingEntity.getBunkCount());
        nursingDTO.setWorkerCount(nursingEntity.getWorkerCount());
        nursingDTO.setSize(nursingEntity.getSize());
        nursingDTO.setAptitude(nursingEntity.getAptitude());
        nursingDTO.setLocation(nursingEntity.getLocation());
        nursingDTO.setImages(images);

        MPJLambdaQueryWrapper<ImageEntity> imageQueryWrapper = new MPJLambdaQueryWrapper<>();
        imageQueryWrapper.selectAll(ImageEntity.class);
        imageQueryWrapper.eq(ImageEntity::getNursingId, nursingDTO.getId());
        List<ImageEntity> list = imageService.list(imageQueryWrapper);
        if (!list.isEmpty()) {
            nursingDTO.setInfoImage(list.get(0).getSrc());
        }

        return ResultData.success(nursingDTO);
    }

    @Operation(summary = "更新养老院信息")
    @PostMapping("/nursing/update")
    public ResultData<String> newsUpdate(@Schema(name = "id", description = "养老院id") @RequestParam("id") Integer id,
                                         @Schema(name = "name", description = "名称") @RequestParam("name") String name,
                                         @Schema(name = "address", description = "地址") @RequestParam("address") String address,
                                         @Schema(name = "phone", description = "电话") @RequestParam("phone") String phone,
                                         @Schema(name = "content", description = "介绍") @RequestParam("content") String content,
                                         @Schema(name = "time", description = "营业时间") @RequestParam("time") String time,
                                         @Schema(name = "bunkCount", description = "床位数量") @RequestParam("bunkCount") Integer bunkCount,
                                         @Schema(name = "workerCount", description = "职工数量") @RequestParam("workerCount") Integer workerCount,
                                         @Schema(name = "size", description = "面积大小") @RequestParam("size") Long size,
                                         @Schema(name = "aptitude", description = "资质等级") @RequestParam("aptitude") Integer aptitude,
                                         @Schema(name = "location", description = "经纬度信息") @RequestParam("location") String location,
                                         @RequestHeader("token") String token,
                                         @RequestPart(name = "files", value = "files", required = false) MultipartFile[] files) {
        // 校验参数是否为空
        if (!CheckParamUtil.nursingCheck(id, name, address, phone, content, time, bunkCount, workerCount, size, aptitude)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        // 更新新闻
        try {
            if (AuthUtil.isAuth(token, userService)) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
            }

            NursingEntity nursingEntity = nursingService.getById(id);
            if (nursingEntity == null) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "养老院不存在");
            }
            nursingEntity.setName(name);
            nursingEntity.setAddress(address);
            nursingEntity.setPhone(phone);
            nursingEntity.setContent(content);
            nursingEntity.setTime(time);
            nursingEntity.setBunkCount(bunkCount);
            nursingEntity.setWorkerCount(workerCount);
            nursingEntity.setSize(size);
            nursingEntity.setAptitude(aptitude);
            nursingEntity.setLocation(location);

            boolean isUpdate = nursingService.updateById(nursingEntity);
            if (!isUpdate) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "养老院信息更新失败");
            }

            List<ImageEntity> imageList = imageService.NursingImagesById(id);
            if (files != null) {
                // 删除原图片url
                for (ImageEntity imageEntity : imageList) {
                    FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, nursingPath);
                }
                boolean isRemove = imageService.removeByIds(imageList);
                if (!isRemove) {
                    return ResultData.fail(ReturnCode.RC500.getCode(), "养老院图片删除失败");
                }

                ArrayList<String> urls = FileUtil.getImageUrl("nursing", files, currentPath, picturePath, picturePath_mapping, nursingPath, String.valueOf(ip_port));
                for (String url : urls) {
                    ImageEntity imageEntity = new ImageEntity();
                    imageEntity.setSrc(url);
                    imageEntity.setNursingId(nursingEntity.getId());
                    boolean isUpdateImage = imageService.saveOrUpdate(imageEntity);
                    if (!isUpdateImage) {
                        for (String u : urls) {
                            FileUtil.deleteImage(u, currentPath, picturePath, nursingPath);
                        }
                        return ResultData.fail(ReturnCode.RC500.getCode(), "图片插入或更新失败");
                    }
                }
            }
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        return ResultData.success("更新养老院信息成功");
    }

    @Operation(summary = "删除养老院信息")
    @Parameters({
            @Parameter(name = "id", description = "养老院ID", required = true)
    })
    @GetMapping("/nursing/delete")
    public ResultData<String> nursingDelete(@RequestParam("id") Integer id, @RequestHeader("token") String token) {
        if (id == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        NursingEntity nursingEntity = nursingService.getById(id);
        if (nursingEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "养老院不存在");
        }

        List<ImageEntity> imageList = imageService.NursingImagesById(id);
        for (ImageEntity imageEntity : imageList) {
            FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, nursingPath);
        }


        DeleteJoinWrapper<ImageEntity> deleteJoinWrapper = JoinWrappers.delete(ImageEntity.class)
                .deleteAll()
                .leftJoin(NursingEntity.class, NursingEntity::getId, ImageEntity::getNursingId)
                .eq(ImageEntity::getNursingId, id);

        boolean isDelete = imageService.deleteJoin(deleteJoinWrapper);
        if (!isDelete) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "删除养老院信息失败");
        }

        return ResultData.success("删除养老院信息成功");
    }

    @Operation(summary = "预约养老院")
    @PostMapping("/nursing/booking")
    public ResultData<String> nursingBooking(@RequestBody NursingBookingDTO nursingBookingDTO, @RequestHeader("token") String token) {
        // 校验参数是否为空
        if (nursingBookingDTO == null || nursingBookingDTO.getNursingId() == null ||
                nursingBookingDTO.getName() == null || nursingBookingDTO.getAddress() == null ||
                nursingBookingDTO.getPhone() == null && nursingBookingDTO.getTime() == null ||
                nursingBookingDTO.getContent() == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        NursingBookingEntity bookingEntity = new NursingBookingEntity();
        String userId = JWTUtil.getId(token);
        if (userId == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户未登录");
        }
        bookingEntity.setUserId(Integer.parseInt(userId));
        bookingEntity.setNursingId(nursingBookingDTO.getNursingId());
        bookingEntity.setName(nursingBookingDTO.getName());
        bookingEntity.setAddress(nursingBookingDTO.getAddress());
        bookingEntity.setPhone(nursingBookingDTO.getPhone());
        bookingEntity.setContent(nursingBookingDTO.getContent());

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(nursingBookingDTO.getTime()), ZoneId.systemDefault());
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        bookingEntity.setTime(timestamp);
        bookingEntity.setContent(nursingBookingDTO.getContent());
        bookingEntity.setStatus(0);

        boolean save = nursingBookingService.save(bookingEntity);
        if (!save) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "预约失败");
        }

        return ResultData.success("预约成功");
    }
}
