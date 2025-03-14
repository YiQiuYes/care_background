package com.linping.care.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.linping.care.dto.NewsDTO;
import com.linping.care.entity.*;
import com.linping.care.service.ImageService;
import com.linping.care.service.NewsService;
import com.linping.care.service.UserService;
import com.linping.care.utils.FileUtil;
import com.linping.care.utils.JWTUtil;
import com.linping.care.utils.NewsUtil;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

@Tag(name = "新闻控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class NewsController {
    private final String currentPath = System.getProperty("user.dir");

    @Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.news-path}")
    private String newsPath;

    @Value("${server.port}")
    private int ip_port;

    private final NewsService newsService;

    private final UserService userService;

    private final ImageService imageService;

    @Operation(summary = "获取新闻列表")
    @Parameters({
            @Parameter(name = "type", description = "新闻类型", required = true, schema = @Schema(allowableValues = {"new", "notice", "policy", "all"})),
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true)
    })
    @GetMapping("/news/list")
    public ResultData<Object> newsList(@RequestParam("type") String type,
                                       @RequestParam(value = "pageNow", defaultValue = "1") int pageNow,
                                       @RequestParam(value = "pageSize", defaultValue = "30") int pageSize) {
        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(400, "页码或页数错误");
        }
        HashMap<String, Object> newsList = newsService.getNewsList(type, pageNow, pageSize);
        if (newsList == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取新闻列表失败");
        }

        return ResultData.success(newsList);
    }

    @Operation(summary = "新闻轮播图")
    @GetMapping("/news/slide")
    public ResultData<ArrayList<Object>> newsSlide() {
        return ResultData.success(newsService.getNewsSlide());
    }

    @Operation(summary = "插入新闻")
    @PostMapping("/news/insert")
    public ResultData<String> newsInsert(@Schema(name = "title", description = "标题") @RequestParam("title") String title,
                                         @Schema(name = "content", description = "内容") @RequestParam("content") String content,
                                         @Schema(name = "source", description = "来源") @RequestParam("source") String source,
                                         @Schema(name = "type", description = "类型", allowableValues = {"new", "notice", "policy"}) @RequestParam("type") String type,
                                         @Schema(name = "createTime", description = "创建时间") @RequestParam("createTime") Long createTime,
                                         @RequestHeader("token") String token,
                                         @RequestPart MultipartFile file) {
        // 校验参数是否为空
        if (!NewsUtil.newsCheck(title, content, source, type, createTime)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        DecodedJWT tokenInfo = JWTUtil.getTokenInfo(token);
        String id = tokenInfo.getClaim("id").asString();

        // 插入新闻
        try {
            UserEntity userEntity = userService.getById(id);

            if (userEntity.getAuth() <= 1) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
            }

            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setTitle(title);
            newsEntity.setContent(content);
            String fileUrl = FileUtil.getImageUrl("news", file, currentPath, picturePath, picturePath_mapping, newsPath, String.valueOf(ip_port));

            newsEntity.setSource(source);
            newsEntity.setType(type);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createTime), ZoneId.systemDefault());
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            newsEntity.setCreateTime(timestamp);
            boolean isInsert = newsService.insertNews(newsEntity);
            if (isInsert) {
                // 插入image数据库
                ImageEntity imageEntity = new ImageEntity();
                imageEntity.setSrc(fileUrl);
                // 获取图片id
                Integer newsEntityId = newsEntity.getId();
                imageEntity.setNewsId(newsEntityId);
                boolean save = imageService.save(imageEntity);
                if (!save) {
                    FileUtil.deleteImage(fileUrl, currentPath, picturePath, newsPath);
                    return ResultData.fail(ReturnCode.RC500.getCode(), "图片插入失败");
                }
                return ResultData.success("插入成功");
            } else {
                // 删除图片
                FileUtil.deleteImage(fileUrl, currentPath, picturePath, newsPath);
                return ResultData.fail(ReturnCode.RC500.getCode(), "资讯插入失败");
            }
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "更新新闻")
    @PostMapping("/news/update")
    public ResultData<String> newsUpdate(@Schema(name = "id", description = "新闻ID") @RequestParam("id") Integer id,
                                         @Schema(name = "title", description = "标题") @RequestParam("title") String title,
                                         @Schema(name = "content", description = "内容") @RequestParam("content") String content,
                                         @Schema(name = "source", description = "来源") @RequestParam("source") String source,
                                         @Schema(name = "type", description = "类型", allowableValues = {"new", "notice", "policy"}) @RequestParam("type") String type,
                                         @Schema(name = "createTime", description = "创建时间") @RequestParam("createTime") Long createTime,
                                         @RequestHeader("token") String token,
                                         @RequestPart(required = false) MultipartFile file) {
        // 校验参数是否为空
        if (!NewsUtil.newsCheck(id, title, content, source, type, createTime)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        DecodedJWT tokenInfo = JWTUtil.getTokenInfo(token);
        String userId = tokenInfo.getClaim("id").asString();

        // 更新新闻
        try {
            UserEntity userEntity = userService.getById(userId);

            if (userEntity.getAuth() <= 1) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
            }

            NewsEntity newsEntity = newsService.getById(id);
            if (newsEntity == null) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "新闻不存在");
            }
            newsEntity.setTitle(title);
            newsEntity.setContent(content);
            newsEntity.setSource(source);
            newsEntity.setType(type);

            ImageEntity imageEntity = imageService.getByNewsId(id);
            if (imageEntity == null) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "图片不存在");
            }

            if (file != null) {
                String fileUrl = FileUtil.getImageUrl("news", file, currentPath, picturePath, picturePath_mapping, newsPath, String.valueOf(ip_port));
                // 删除原图片url
                FileUtil.deleteImage(imageEntity.getSrc(), currentPath, picturePath, newsPath);
                imageEntity.setSrc(fileUrl);
                boolean isUpdateImage = imageService.updateById(imageEntity);
                if (!isUpdateImage) {
                    FileUtil.deleteImage(fileUrl, currentPath, picturePath, newsPath);
                    return ResultData.fail(ReturnCode.RC500.getCode(), "图片更新失败");
                }
            }

            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createTime), ZoneId.systemDefault());
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            newsEntity.setCreateTime(timestamp);
            boolean isUpdate = newsService.updateById(newsEntity);
            if (isUpdate) {
                return ResultData.success("更新成功");
            } else {
                return ResultData.fail(ReturnCode.RC500.getCode(), "更新失败");
            }
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "根据id获取新闻内容")
    @Parameters({
            @Parameter(name = "id", description = "新闻ID", required = true)
    })
    @GetMapping("/news/getNewById")
    public ResultData<NewsDTO> getNewById(@RequestParam("id") Integer id) {
        if (id == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }
        NewsEntity newsEntity = newsService.getById(id);
        if (newsEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "新闻不存在");
        }

        NewsDTO newsDTO =  newsService.getNewsDetailById(id);
        return ResultData.success(newsDTO);
    }
}
