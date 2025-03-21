package com.linping.care.controller;

import com.linping.care.dto.OrdersDTO;
import com.linping.care.entity.OrdersEntity;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.service.OrdersService;
import com.linping.care.service.UserService;
import com.linping.care.utils.AuthUtil;
import com.linping.care.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "订单控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;

    private final UserService userService;

    @Operation(summary = "插入订单")
    @PostMapping("/orders/insert")
    public ResultData<String> ordersInsert(@RequestBody List<OrdersDTO> ordersDTOs, @RequestHeader("token") String token) {
        for (OrdersDTO ordersDTO : ordersDTOs) {
            if (ordersDTO.getPhone().isEmpty() || ordersDTO.getAddress().isEmpty() || ordersDTO.getGoodsId() == null || ordersDTO.getCount() == null) {
                return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
            }
        }

        String id = JWTUtil.getId(token);
        boolean insert = ordersService.orderInsert(ordersDTOs, Integer.valueOf(id));
        if (insert) {
            return ResultData.success("订单创建成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "订单创建失败");
        }
    }

    @Operation(summary = "获取订单组")
    @GetMapping("/orders/map")
    public ResultData<Object> ordersMap(@RequestHeader("token") String token) {
        String id = JWTUtil.getId(token);
        Map<String, List<OrdersEntity>> map = ordersService.ordersMap(Integer.valueOf(id));
        return ResultData.success(map);
    }

    @Operation(summary = "根据商品类型获取订单列表")
    @GetMapping("/orders/typeList")
    @Parameters({
            @Parameter(name = "type", description = "商品类型", required = true,
                    schema = @Schema(allowableValues = {"common", "housekeeping", "medicalCare",
                    "ageingAtHome", "goods", "device", "healthCare"})),
            @Parameter(name = "pageNow", description = "当前页码", required = true),
            @Parameter(name = "pageSize", description = "每页条数", required = true),
            @Parameter(name = "status", description = "订单状态")
    })
    public ResultData<Object> ordersTypeList(@RequestParam("type") String type,
                                             @RequestParam(value = "pageNow", defaultValue = "1") Integer pageNow,
                                             @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
                                             @RequestParam(value = "status", required = false) Integer status,
                                             @RequestHeader("token") String token) {
        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        if (pageNow <= 0 || pageSize <= 0) {
            return ResultData.fail(400, "页码或页数错误");
        }

        if (type == null || type.isEmpty()) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        HashMap<String, Object> map = ordersService.ordersTypeList(type, status, pageNow, pageSize);
        return ResultData.success(map);
    }

    @Operation(summary = "修改订单状态")
    @GetMapping("/orders/modifyStatus")
    @Parameters({
            @Parameter(name = "id", description = "订单id"),
            @Parameter(name = "status", description = "订单状态")
    })
    public ResultData<Object> modifyStatus(@RequestParam("id") Integer id, @RequestParam(value = "status") Integer status, @RequestHeader("token") String token) {
        if (AuthUtil.isAuth(token, userService)) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "权限不足");
        }

        if (id == null || status == null) {
            return ResultData.fail(ReturnCode.RC500.getCode(), "参数错误");
        }

        boolean isSuccess = ordersService.modifyStatusById(id, status);
        if (isSuccess) {
            return ResultData.success("订单状态修改成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "订单状态修改失败");
        }
    }

    @Operation(summary = "用户根据状态获取订单列表")
    @GetMapping("/orders/statusList")
    @Parameters({
            @Parameter(name = "status", description = "订单状态")
    })
    public ResultData<Object> orderStatusList(@RequestParam(value = "status") Integer status, @RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        List<OrdersDTO> ordersDTOS = ordersService.orderStatusList(Integer.valueOf(userId), status);
        return ResultData.success(ordersDTOS);
    }

    @Operation(summary = "确认收货")
    @GetMapping("/orders/confirmReceive")
    @Parameters({
            @Parameter(name = "id", description = "订单id")
    })
    public ResultData<Object> confirmReceive(@RequestParam("id") Integer id, @RequestHeader("token") String token) {
        String userId = JWTUtil.getId(token);
        boolean isSuccess = ordersService.confirmReceive(id, Integer.valueOf(userId));
        if (isSuccess) {
            return ResultData.success("确认收货成功");
        } else {
            return ResultData.fail(ReturnCode.RC500.getCode(), "确认收货失败");
        }
    }
}
