package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(name = "OrdersDTO", description = "订单信息类")
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDTO {
    @Schema(name = "id", description = "主键id")
    private Integer id;
    @Schema(name = "goodsId", description = "商品id列表")
    private Integer goodsId;
    @Schema(name = "count", description = "商品数量")
    private GoodsDTO goodsDTO;
    @Schema(name = "price", description = "实付价格")
    private BigDecimal price;
    @Schema(name = "address", description = "收货地址")
    private String address;
    @Schema(name = "createTime", description = "订单创建时间")
    private Long createTime;
    @Schema(name = "status", description = "订单状态")
    private Integer status;
    @Schema(name = "phone", description = "联系电话")
    private String phone;
    @Schema(name = "count", description = "商品数量")
    private Integer count;
}
