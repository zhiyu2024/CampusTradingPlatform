package cn.gdsdxy.campustrading;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = {
        "cn.gdsdxy.campustrading"      // 添加 common 模块扫描
})
@MapperScan("cn.gdsdxy.campustrading.common.mapper") // ✅ 添加这行
public class CampusTradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusTradingApplication.class, args);
    }

}
