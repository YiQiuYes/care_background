package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Schema(name="GoodsDTO",description ="商品信息类" )
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDTO {
    @Schema(name = "id",description = "主键id")
    private Integer id;
    @Schema(name = "name",description = "商品名称")
    private String name;
    @Schema(name = "description",description = "商品描述")
    private String description;
    @Schema(name = "type",description = "商品类型", allowableValues = {"common"})
    private String type;
    @Schema(name = "price",description = "商品价格")
    private BigDecimal price;
    @Schema(name = "imageSrc",description = "商品图片")
    private String imageSrc;
    @Schema(name = "isActive",description = "是否激活")
    private Integer isActive;
    @Schema(name = "createTime",description = "创建时间")
    private Timestamp createTime;
}
