package com.linping.care.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class ResultData<T> {
    // 结果状态码
    private int code;

    // 响应信息
    private String msg;

    // 响应数据
    private T data;

    // 接口请求时间
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<T>();
        resultData.setCode(ReturnCode.RC200.getCode());
        resultData.setMsg(ReturnCode.RC200.getMsg());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(int code, String msg) {
        ResultData<T> resultData = new ResultData<T>();
        resultData.setCode(code);
        resultData.setMsg(msg);
        return resultData;
    }

    public String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }
}
