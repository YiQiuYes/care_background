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
                                          @Schema(name = "createTime", description = "创建时间") @RequestParam("createTime") Timestamp createTime,
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
        goodsDTO.setCreateTime(createTime);

        GoodsEntity goodsEntity = goodsService.insertGoods(goodsDTO);
        if (goodsEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "插入商品失败");
        }

        String fileUrl;
        try {
            fileUrl = FileUtil.getImageUrl("goods", file, currentPath, picturePath, picturePath_mapping, goodsPath, String.valueOf(ip_port));
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
                                          @Schema(name = "isActive", description = "创建时间") @RequestParam("isActive") Integer isActive,
                                          @Schema(name = "createTime", description = "创建时间") @RequestParam("createTime") Timestamp createTime,
                                          @RequestHeader("token") String token,
                                          @RequestPart(name = "file", value = "file", required = false) MultipartFile file) {
        if (id == null || name == null || type == null || isActive == null || createTime == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setId(id);
        goodsDTO.setName(name);
        goodsDTO.setDescription(description);
        goodsDTO.setType(type);
        goodsDTO.setPrice(price);
        goodsDTO.setIsActive(isActive);
        goodsDTO.setCreateTime(createTime);

        boolean updateById = goodsService.updateGoods(goodsDTO);
        if (!updateById) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "更新商品失败");
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
            fileUrl = FileUtil.getImageUrl("goods", file, currentPath, picturePath, picturePath_mapping, goodsPath, String.valueOf(ip_port));
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
}
