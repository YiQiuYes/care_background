package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Schema(name="NewsDTO",description ="资讯信息" )
@AllArgsConstructor
@NoArgsConstructor
public class NewsDTO {
    @Schema(name = "id",description = "主键id")
    private Integer id;
    @Schema(name = "title",description = "标题")
    private String title;
    @Schema(name = "content",description = "内容")
    private String content;
    @Schema(name = "imageSrc",description = "图片地址")
    private String imageSrc;
    @Schema(name = "source",description = "来源")
    private String source;
    @Schema(name = "type",description = "类型")
    private String type;
    @Schema(name = "createTime",description = "创建时间")
    private Timestamp createTime;
}
