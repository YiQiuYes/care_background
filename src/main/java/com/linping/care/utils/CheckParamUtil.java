package com.linping.care.utils;

public class CheckParamUtil {
    // 检测新闻是否存在
    public static boolean newsCheck(Integer id, String title, String content, String source, String type, Long createTime) {
        // 校验参数是否为空
        return id != null && title != null && content != null && source != null && type != null && createTime != null;
    }

    public static boolean newsCheck(String title, String content, String source, String type, Long createTime) {
        // 校验参数是否为空
        return title != null && content != null && source != null && type != null && createTime != null;
    }

    // 检测养老院参数是否存在
    public static boolean nursingCheck(Integer id, String name, String address, String phone,
                                       String content, String time, Integer bunkCount, Integer workerCount,
                                       Long size, Integer aptitude) {
        // 校验参数是否为空
        return id != null && name != null && address != null && phone != null && content != null && time != null && bunkCount != null && workerCount != null && size != null && aptitude != null;
    }
}
