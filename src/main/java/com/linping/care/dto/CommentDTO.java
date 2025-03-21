package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "CommentDTO", description = "评论信息类")
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @Schema(name = "id", description = "主键id")
    private Integer id;
    @Schema(name = "userId", description = "数量")
    private Integer userId;
    @Schema(name = "goodsId", description = "订单id")
    private Integer goodsId;
    @Schema(name = "ordersId", description = "商品id")
    private Integer ordersId;
    @Schema(name = "grade", description = "商品评分")
    private Integer grade;
    @Schema(name = "content", description = "商品评价")
    private String content;
}
