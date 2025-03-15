package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(name="NursingDTO",description ="养老院信息" )
@AllArgsConstructor
@NoArgsConstructor
public class NursingDTO {
    @Schema(name = "id",description = "主键id")
    private Integer id;
    @Schema(name = "name",description = "名称")
    private String name;
    @Schema(name = "address",description = "地址")
    private String address;
    @Schema(name = "phone",description = "电话")
    private String phone;
    @Schema(name = "content",description = "介绍")
    private String content;
    @Schema(name = "time",description = "营业时间")
    private String time;
    @Schema(name = "bunkCount",description = "床位数量")
    private Integer bunkCount;
    @Schema(name = "workerCount",description = "职工数量")
    private Integer workerCount;
    @Schema(name = "size",description = "面积大小")
    private Long size;
    @Schema(name = "aptitude",description = "资质等级")
    private Integer aptitude;
    @Schema(name = "images",description = "养老院图片")
    private List<String> images;
    @Schema(name = "infoImage",description = "养老院介绍图片")
    private String infoImage;
    @Schema(name = "location",description = "经纬度")
    private String location;
}
