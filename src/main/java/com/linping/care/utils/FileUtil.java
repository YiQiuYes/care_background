package com.linping.care.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
public class FileUtil {
    // picturePath=/images   picturePath_mapping=/images   newsPath=/news
    public static String getImageUrl(String fileHeader, MultipartFile file, String currentPath, String picturePath, String picturePath_mapping, String typePath, String ip_port) throws IOException {
        String fileName = file.getOriginalFilename();  // 文件名
        File dest;
        if (fileName != null) {
            dest = generateFile(fileName, fileHeader, currentPath, picturePath, typePath);
        } else {
            throw new RuntimeException("文件后缀名错误");
        }

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败");
        }
        String ip = InetAddress.getLocalHost().getHostAddress();
        return "http://" + ip + ":" + ip_port + picturePath_mapping + typePath + "/" + dest.getName();
    }

    public static String getImageUrl(String fileHeader, File file, String currentPath, String picturePath, String picturePath_mapping, String typePath, String ip_port) throws IOException {
        String fileName = file.getName();  // 文件名
        File dest = generateFile(fileName, fileHeader, currentPath, picturePath, typePath);

        // 复制文件
        try (InputStream input = new FileInputStream(file); OutputStream output = new FileOutputStream(dest)) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String ip = InetAddress.getLocalHost().getHostAddress();
        return "http://" + ip + ":" + ip_port + picturePath_mapping + typePath + "/" + dest.getName();
    }

    public static ArrayList<String> getImageUrl(String fileHeader, MultipartFile[] files, String currentPath, String picturePath, String picturePath_mapping, String typePath, String ip_port) throws IOException {
        String ip = InetAddress.getLocalHost().getHostAddress();

        ArrayList<String> urls = new ArrayList<>();
        // 遍历files
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();  // 文件名
            File dest;
            if (fileName != null) {
                dest = generateFile(fileName, fileHeader, currentPath, picturePath, typePath);
            } else {
                throw new RuntimeException("文件后缀名错误");
            }

            try {
                file.transferTo(dest);
                urls.add("http://" + ip + ":" + ip_port + picturePath_mapping + typePath + "/" + dest.getName());
            } catch (IOException e) {
                throw new RuntimeException("文件上传失败");
            }
        }

        return urls;
    }

    public static void deleteImage(String url, String currentPath, String picturePath, String typePath) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        String path = currentPath + picturePath + typePath + "/" + fileName;
        File file = new File(path);
        boolean delete = file.delete();
        if (!delete) {
            log.info("uri: {} 删除文件失败", url);
        }
    }

    private static File generateFile(String fileName, String fileHeader, String currentPath, String picturePath, String newsPath) {
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String finalFileName = fileHeader + "-" + UUID.randomUUID() + suffixName; // 新文件名
        File dest = new File(currentPath + picturePath + newsPath + "/" + finalFileName);
        if (!dest.getParentFile().exists()) {
            boolean mkdir = dest.getParentFile().mkdirs();
            if (!mkdir) {
                throw new RuntimeException("创建文件失败");
            }
        }
        return dest;
    }
}
