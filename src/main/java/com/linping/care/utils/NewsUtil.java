package com.linping.care.utils;

public class NewsUtil {
    // 检测新闻是否存在
    public static boolean newsCheck(Integer id, String title, String content, String source, String type, Long createTime) {
        // 校验参数是否为空
        return id != null && title != null && content != null && source != null && type != null && createTime != null;
    }

    public static boolean newsCheck(String title, String content, String source, String type, Long createTime) {
        // 校验参数是否为空
        return title != null && content != null && source != null && type != null && createTime != null;
    }
}
