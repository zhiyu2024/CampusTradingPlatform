package cn.gdsdxy.campustrading.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component // ✅ 改为注入式组件，而非静态类
public class FileUploadUtils { // ✅ 改名避免与 java.io.FileUtils 冲突

    @Value("${file.upload-images-path}") // ✅ 注入配置文件中的路径
    private String uploadImagesPath;

    /**
     * ✅ 上传文件并返回可访问的URL
     * @param file 前端传来的文件
     * @return 完整的访问URL，如 /res/images/uuid.png
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        try {
            // 1. 生成新文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = FileUtil.getSuffix(originalFilename);
            String newFileName = IdUtil.simpleUUID() + "." + suffix;

            // 2. 确保目录存在（创建到 images 文件夹）
            File dir = new File(uploadImagesPath);
            if (!dir.exists()) {
                dir.mkdirs(); // 如果路径是 D:/.../res/images，会创建 images 文件夹
            }

            // 3. 保存文件到目标位置
            File destFile = new File(dir, newFileName);
            file.transferTo(destFile);

            // 4. ✅ 返回可访问的URL（与 WebMvcConfig 映射一致）
            return "/res/images/" + newFileName; // 前端直接用这个路径访问

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量上传（可选）
     */
    public List<String> uploadBatch(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(this::upload)
                .collect(Collectors.toList());
    }
}