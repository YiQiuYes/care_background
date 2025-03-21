package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "CartDTO", description = "购物车信息类")
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    @Schema(name = "id", description = "主键id")
    private Integer id;
    @Schema(name = "goodsDTO", description = "购物车商品列表")
    private GoodsDTO goodsDTO;
    @Schema(name = "count", description = "数量")
    private Integer count;
}
