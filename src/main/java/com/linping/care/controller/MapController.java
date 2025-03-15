package com.linping.care.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.utils.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Tag(name = "地图控制类")
@RestController
@Slf4j
@RequiredArgsConstructor
public class MapController {
    @Value("${map.key}")
    private String mapKey;

    @Operation(summary = "根据地址转经纬度")
    @GetMapping("/map/conversion")
    public ResultData<Object> conversion(String address) {
        String url = "https://restapi.amap.com/v3/geocode/geo?parameters";
        String result;
        HashMap<String, String> param = new HashMap<>();
        param.put("key", mapKey);
        param.put("address", address);
        result = RequestUtil.get(url, param);

        // 将result字符串转为json对象
        if (result != null) {
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                // 判断status是否为0
                if ("0".equals(status)) {
                    return ResultData.fail(ReturnCode.RC500.getCode(), "地图Key无效");
                }
            }
            return ResultData.success(jsonObject);
        }

        return ResultData.fail(ReturnCode.RC500.getCode(), "获取地图坐标失败");
    }
}
