package com.linping.care.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnCode {
    RC200(200, "操作成功"),
    RC400(400, "Token过期"),
    RC401(401, "Token签名无效"),
    RC402(402, "Token无效"),
    RC403(403, "Token算法不一致"),
    RC500(500, "登录失败");

    // 自定义状态码
    private final int code;

    // 自定义描述
    private final String msg;
}
