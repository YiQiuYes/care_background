package com.linping.care.controller;

import com.linping.care.entity.News;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.service.NewsService;
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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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

    @Value("${server.port}")
    private int ip_port;

    private final NewsService newsService;

    @Operation(summary = "获取新闻列表")
    @Parameters({
            @Parameter(name = "type", description = "新闻类型", required = true, schema = @Schema(allowableValues = {"new", "notice", "policy"})),
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

        return ResultData.success(newsService.getNewsList(type, pageNow, pageSize));
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
                                         @Schema(name = "createTime", description = "创建时间") @RequestParam("createTime") long createTime,
                                         @RequestPart MultipartFile file) {
        // 校验参数是否为空
        if (title == null || content == null || source == null || type == null || file == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        // 插入新闻
        try {
            News news = new News();
            news.setTitle(title);
            news.setContent(content);

            String fileName = file.getOriginalFilename();  // 文件名
            String suffixName;  // 后缀名
            if (fileName != null) {
                suffixName = fileName.substring(fileName.lastIndexOf("."));
            } else {
                return ResultData.fail(ReturnCode.RC500.getCode(), "文件后缀名错误");
            }
            fileName = "news-" + UUID.randomUUID() + suffixName; // 新文件名
            File dest = new File(currentPath + picturePath + fileName);
            if (!dest.getParentFile().exists()) {
                boolean mkdir = dest.getParentFile().mkdirs();
                if (!mkdir) {
                    return ResultData.fail(ReturnCode.RC500.getCode(), "创建文件夹失败");
                }
            }
            try {
                file.transferTo(dest);
            } catch (IOException e) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "文件上传失败");
            }
            String ip = InetAddress.getLocalHost().getHostAddress();
            String final_fileName = "http://" + ip + ":" + ip_port + picturePath_mapping + fileName;
            news.setImageSrc(final_fileName);
            news.setSource(source);
            news.setType(type);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createTime), ZoneId.systemDefault());
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            news.setCreateTime(timestamp);
            boolean isInsert = newsService.insertNews(news);
            if (isInsert) {
                return ResultData.success("插入成功");
            } else {
                return ResultData.fail(ReturnCode.RC500.getCode(), "插入失败");
            }
        } catch (Exception e) {
            return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
        }
    }
}
