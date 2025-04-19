package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(name = "BedDTO", description = "床位信息类")
@AllArgsConstructor
@NoArgsConstructor
public class BedDTO {
    @Schema(name = "id", description = "主键id")
    private Integer id;
    @Schema(name = "nursingId", description = "养老院id")
    private Integer nursingId;
    @Schema(name = "size", description = "面积大小")
    private Integer size;
    @Schema(name = "address", description = "地址")
    private String address;
    @Schema(name = "status", description = "预定状态")
    private Integer status;
    @Schema(name = "meta", description = "标签")
    private String meta;
    @Schema(name = "price", description = "价格")
    private BigDecimal price;
    @Schema(name = "description", description = "床位描述")
    private String description;
    @Schema(name = "aptitude", description = "资质等级")
    private Integer aptitude;
    @Schema(name = "imageSrc", description = "图片地址")
    private String imageSrc;
}
