package cn.gdsdxy.campustrading.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer { //  正确实现接口

    @Value("${file.upload-images-path}") //  使用正确的配置项
    private String uploadImagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 1. 映射本地图片：访问 /res/images/xxx.jpg -> 本地目录
        // 注意：必须以 file: 开头，且路径最后要有 /
        registry.addResourceHandler("/res/images/**")
                .addResourceLocations("file:" + uploadImagesPath + "/");

        // 2. Knife4j 文档
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        // 3. Swagger UI 文档
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        //  4. Swagger 公共资源
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}