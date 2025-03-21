package com.linping.care.controller;

import com.linping.care.entity.CartEntity;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.entity.UserEntity;
import com.linping.care.service.CartService;
import com.linping.care.service.UserService;
import com.linping.care.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Tag(name = "购物车控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    private final UserService userService;

    @Operation(summary = "获取购物车列表")
    @Parameters({
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true)
    })
    @GetMapping("/cart/list")
    public ResultData<Object> cartList(@RequestParam(value = "pageNow", defaultValue = "1") int pageNow,
                                       @RequestParam(value = "pageSize", defaultValue = "30") int pageSize,
                                       @RequestHeader("token") String token) {
        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(400, "页码或页数错误");
        }

        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        HashMap<String, Object> cartList = cartService.getCartList(Integer.valueOf(userId), pageNow, pageSize);
        if (cartList == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "获取购物车列表失败");
        }

        return ResultData.success(cartList);
    }

    @Operation(summary = "插入购物车列表")
    @Parameters({
            @Parameter(name = "goodsId", description = "商品id", required = true),
            @Parameter(name = "count", description = "商品数量", required = true)
    })
    @GetMapping("/cart/insert")
    public ResultData<Object> insertCart(@RequestParam(value = "goodsId") Integer goodsId,
                                         @RequestParam(value = "count") Integer count,
                                         @RequestHeader("token") String token) {
        if (goodsId <= 0 || count <= 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "商品id或数量错误");
        }

        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        CartEntity cartEntity = new CartEntity();
        cartEntity.setUserId(Integer.valueOf(userId));
        cartEntity.setGoodsId(goodsId);
        cartEntity.setCount(count);

        boolean isSuccess = cartService.save(cartEntity);
        if (!isSuccess) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "插入购物车失败");
        }

        return ResultData.success("插入购物车成功");
    }

    @Operation(summary = "删除购物车列表")
    @Parameters({
            @Parameter(name = "id", description = "购物车id", required = true)
    })
    @GetMapping("/cart/delete")
    public ResultData<Object> deleteCart(@RequestParam(value = "id") Integer id,
                                         @RequestHeader("token") String token) {
        if (id <= 0) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "购物车id错误");
        }

        String userId = JWTUtil.getId(token);
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "用户不存在");
        }

        boolean isSuccess = cartService.removeById(id);
        if (!isSuccess) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "删除购物车失败");
        }

        return ResultData.success("删除购物车成功");
    }
}
