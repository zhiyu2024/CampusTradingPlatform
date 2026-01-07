package cn.gdsdxy.campustrading.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    // 定义上传的基础路径 (开发环境可以写死，生产环境建议读配置)
    // 注意：Windows 下是 D:/... Linux 下是 /home/...
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    /**
     * 上传文件
     * @param file 前端传来的文件
     * @return 存储后的文件名
     */
    public static String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 1. 获取原文件名
        String originalFilename = file.getOriginalFilename();

        // 2. 生成新文件名 (防止重名覆盖)，例如：uuid.png
        String suffix = FileUtil.getSuffix(originalFilename);
        String newFileName = IdUtil.simpleUUID() + "." + suffix;

        // 3. 确保存储目录存在
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 保存文件
        try {
            file.transferTo(new File(dir, newFileName));
            return newFileName; // 返回文件名存入数据库
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
