package com.linping.care.controller;

import com.linping.care.dto.GoodsDTO;
import com.linping.care.entity.GoodsEntity;
import com.linping.care.entity.ImageEntity;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.service.GoodsService;
import com.linping.care.service.ImageService;
import com.linping.care.service.UserService;
import com.linping.care.utils.AuthUtil;
import com.linping.care.utils.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

@Tag(name = "商品控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class GoodsController {
    private final String currentPath = System.getProperty("user.dir");

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.goods-path}")
    private String goodsPath;

    @Value("${server.port}")
    private int ip_port;

    @Value("${ip}")
    private String ip;

    private final UserService userService;

    private final GoodsService goodsService;

    private final ImageService imageService;

    @Operation(summary = "插入商品")
    @PostMapping("/goods/insert")
    public ResultData<String> goodsInsert(@Schema(name = "name", description = "商品名称") @RequestParam("name") String name,
                                          @Schema(name = "description", description = "商品描述") @RequestParam("description") String description,
                                          @Schema(name = "type", description = "商品描述", allowableValues = {"common"}) @RequestParam("type") String type,
                                          @Schema(name = "price", description = "商品价格") @RequestParam("price") BigDecimal price,
                                          @Schema(name = "isActive", description = "创建时间") @RequestParam("isActive") Integer isActive,
                                          @Schema(name = "createTime", description = "创建时间") @RequestParam("createTime") Long createTime,
                                          @RequestHeader("token") String token,
                                          @RequestPart MultipartFile file) {
        if (name == null || type == null || isActive == null || createTime == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setName(name);
        goodsDTO.setDescription(description);
        goodsDTO.setType(type);
        goodsDTO.setPrice(price);
        goodsDTO.setIsActive(isActive);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createTime), ZoneId.systemDefault());
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        goodsDTO.setCreateTime(timestamp);

        GoodsEntity goodsEntity = goodsService.insertGoods(goodsDTO);
        if (goodsEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "插入商品失败");
        }

        String fileUrl;
        try {
            fileUrl = FileUtil.getImageUrl("goods", file, currentPath, picturePath, picturePath_mapping, goodsPath, String.valueOf(ip_port), ip);
        } catch (IOException e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }

        // 插入image数据库
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setSrc(fileUrl);
        imageEntity.setGoodsId(goodsEntity.getId());
        boolean save = imageService.save(imageEntity);
        if (!save) {
            // 删除插入的商品
            goodsService.removeById(goodsEntity.getId());
            return ResultData.fail(ReturnCode.RC500.getCode(), "插入图片失败");
        }

        return ResultData.success("更新成功");
    }

    @Operation(summary = "更新商品")
    @PostMapping("/goods/update")
    public ResultData<String> goodsUpdate(@Schema(name = "id", description = "商品ID") @RequestParam("id") Integer id,
                                          @Schema(name = "name", description = "商品名称") @RequestParam("name") String name,
                                          @Schema(name = "description", description = "商品描述") @RequestParam("description") String description,
                                          @Schema(name = "type", description = "商品描述", allowableValues = {"common"}) @RequestParam("type") String type,
                                          @Schema(name = "price", description = "商品价格") @RequestParam("price") BigDecimal price,
                                          @Schema(name = "isActive", description = "是否上架") @RequestParam("isActive") Integer isActive,
                                          @Schema(name = "createTime", description = "创建时间") @RequestParam("createTime") Long createTime,
                                          @RequestHeader("token") String token,
                                          @RequestPart(name = "file", value = "file", required = false) MultipartFile file) {
        if (id == null || name == null || type == null || isActive == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        // 查询商品是否存在
        GoodsEntity goodsEntity = goodsService.getById(id);
        if (goodsEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "商品不存在");
        }

        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setId(id);
        goodsDTO.setName(name);
        goodsDTO.setDescription(description);
        goodsDTO.setType(type);
        goodsDTO.setPrice(price);
        goodsDTO.setIsActive(isActive);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createTime), ZoneId.systemDefault());
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        goodsDTO.setCreateTime(timestamp);

        boolean updateById = goodsService.updateGoods(goodsDTO);
        if (!updateById) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "更新商品失败");
        }

        if (file == null) {
            return ResultData.success("更新成功");
        }

        // 更新图片
        ImageEntity imageEntity = imageService.getByGoodsId(id);
        if (imageEntity == null) {
            imageEntity = new ImageEntity();
            imageEntity.setGoodsId(id);
        }
        // 删除原图片
        String src = imageEntity.getSrc();
        if (src != null) {
            FileUtil.deleteImage(src, currentPath, picturePath, goodsPath);
        }
        // 上传新图片
        String fileUrl;
        try {
            fileUrl = FileUtil.getImageUrl("goods", file, currentPath, picturePath, picturePath_mapping, goodsPath, String.valueOf(ip_port), ip);
        } catch (IOException e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }
        imageEntity.setSrc(fileUrl);
        boolean save = imageService.saveOrUpdate(imageEntity);
        if (!save) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "更新图片失败");
        }

        return ResultData.success("更新成功");
    }

    @Operation(summary = "获取商品列表")
    @Parameters({
            @Parameter(name = "type", description = "商品类型", required = true, schema = @Schema(allowableValues = {"common"})),
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true)
    })
    @GetMapping("/goods/list")
    public ResultData<Object> goodsList(@RequestParam("type") String type,
                                       @RequestParam(value = "pageNow", defaultValue = "1") int pageNow,
                                       @RequestParam(value = "pageSize", defaultValue = "30") int pageSize) {
        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(400, "页码或页数错误");
        }
        HashMap<String, Object> goodsList = goodsService.getGoodsList(type, pageNow, pageSize);
        if (goodsList == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取商品列表失败");
        }

        return ResultData.success(goodsList);
    }

    @Operation(summary = "根据商品ID获取商品")
    @GetMapping("/goods/id")
    @Parameters({
            @Parameter(name = "id", description = "商品ID", required = true)
    })
    public ResultData<Object> goodsById(@RequestParam("id") Integer id) {
        if (id == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        GoodsEntity goodsEntity = goodsService.getById(id);
        if (goodsEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "商品不存在");
        }

        ImageEntity imageEntity = imageService.getByGoodsId(id);
        if (imageEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "商品图片不存在");
        }

        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setId(goodsEntity.getId());
        goodsDTO.setName(goodsEntity.getName());
        goodsDTO.setDescription(goodsEntity.getDescription());
        goodsDTO.setType(goodsEntity.getType());
        goodsDTO.setPrice(goodsEntity.getPrice());
        goodsDTO.setImageSrc(imageEntity.getSrc());
        goodsDTO.setIsActive(goodsEntity.getIsActive());
        goodsDTO.setCreateTime(goodsEntity.getCreateTime());

        return ResultData.success(goodsDTO);
    }
}
