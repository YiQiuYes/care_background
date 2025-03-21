package com.linping.care.controller;

import com.linping.care.dto.CommentDTO;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.service.CommentService;
import com.linping.care.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评价控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "插入评价")
    @Parameters({
            @Parameter(name = "ordersId", description = "订单id", required = true),
            @Parameter(name = "goodsId", description = "商品id", required = true),
            @Parameter(name = "grade", description = "商品评分", required = true),
            @Parameter(name = "content", description = "商品评价", required = true)
    })
    @PostMapping("/comment/insert")
    public ResultData<Object> insertComment(@RequestBody CommentDTO commentDTO,
                                            @RequestHeader("token") String token) {
        if (commentDTO.getOrdersId() == null || commentDTO.getGoodsId() == null || commentDTO.getGrade() == null || commentDTO.getContent() == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        String userId = JWTUtil.getId(token);
        if (userId == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        if (commentService.insertComment(Integer.valueOf(userId), commentDTO.getOrdersId(), commentDTO.getGoodsId(), commentDTO.getContent(), commentDTO.getGrade())) {
            return ResultData.success("插入评价成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "插入评价失败");
        }
    }
}
