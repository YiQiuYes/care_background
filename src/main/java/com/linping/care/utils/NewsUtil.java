package com.linping.care.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

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

    // picturePath=/images   picturePath_mapping=/images   newsPath=/news
    public static String getNewsImageUrl(MultipartFile file, String currentPath, String picturePath, String picturePath_mapping, String newsPath, String ip_port) throws IOException {
        String fileName = file.getOriginalFilename();  // 文件名
        String suffixName;  // 后缀名
        if (fileName != null) {
            suffixName = fileName.substring(fileName.lastIndexOf("."));
        } else {
            throw new RuntimeException("文件后缀名错误");
        }
        fileName = "news-" + UUID.randomUUID() + suffixName; // 新文件名
        File dest = new File(currentPath + picturePath + newsPath + "/" + fileName);
        if (!dest.getParentFile().exists()) {
            boolean mkdir = dest.getParentFile().mkdirs();
            if (!mkdir) {
                throw new RuntimeException("创建文件夹失败");
            }
        }

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败");
        }
        String ip = InetAddress.getLocalHost().getHostAddress();
        return "http://" + ip + ":" + ip_port + picturePath_mapping + newsPath + "/" + fileName;
    }

    public static boolean deleteImage(String url, String currentPath, String picturePath, String newsPath) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        String path = currentPath + picturePath + newsPath + "/" + fileName;
        File file = new File(path);
        return file.delete();
    }
}
