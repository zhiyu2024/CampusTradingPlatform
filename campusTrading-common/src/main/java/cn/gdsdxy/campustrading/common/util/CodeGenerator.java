package cn.gdsdxy.campustrading.common.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.util.Collections;
import java.util.Scanner;

public class CodeGenerator {

    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String input = scanner.next();
            if (!input.isBlank()) {
                return input;
            }
        }
        throw new IllegalArgumentException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // ==================== 关键修改1：项目路径 ====================
        // 获取当前项目根路径：D:/.../CampusTrading
        String projectPath = System.getProperty("user.dir")+"/CampusTrading/campusTrading-common";

        // 输出到common模块（最关键！）
        String outputPath = projectPath + "/src/main/java";
        String resourcePath = projectPath + "/src/main/resources";

        // ==================== 调试用（生产环境可删） ====================
        System.out.println("当前项目路径: " + projectPath);
        System.out.println("Java输出路径: " + outputPath);
        System.out.println("Resources路径: " + resourcePath);

        // 获取表名
        String tableNames = scanner("表名（多个用逗号分割，如users,products）");
        System.out.println("准备生成表: " + tableNames);

        try {
            // 准备 XML 输出路径
            String xmlOutputPath = resourcePath + "/mapper";
            System.out.println("XML输出路径: " + xmlOutputPath);

            FastAutoGenerator.create(
                            "jdbc:mysql://localhost:3306/school?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8",
                            "root",
                            "123456"  // 改成你的密码
                    )
                    .globalConfig(builder -> {
                        builder.outputDir(outputPath)  // ✅ 输出到common模块
                                .author("CampusTrading")
                                .dateType(DateType.ONLY_DATE)
                                .disableOpenDir();
                    })
                    .packageConfig(builder -> {
                        // ==================== 关键修改2：包名 ====================
                        builder.parent("cn.gdsdxy.campustrading.common")  // ✅ 完整包名
                                .moduleName("")          // 模块名为空
                                .entity("entity")        // Entity包
                                .mapper("mapper")        // Mapper包
                                .service("service")      // Service包（可选）
                                .serviceImpl("service.impl")
                                .controller("controller") // Controller包（可选）
                                // XML强制输出到common的resources/mapper
                                .pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputPath));
                    })
                    .strategyConfig(builder -> {
                        builder.addInclude(tableNames.split(","))
                                .entityBuilder()
                                .enableLombok()
                                .enableFileOverride()  // 覆盖旧文件
                                .naming(NamingStrategy.underline_to_camel)
                                .columnNaming(NamingStrategy.underline_to_camel)
                                .formatFileName("%sEntity")  // User -> UserEntity
                                .controllerBuilder()
                                .enableRestStyle()
                                .enableFileOverride()
                                .serviceBuilder()
                                .formatServiceFileName("I%sService")
                                .formatServiceImplFileName("%sServiceImpl")
                                .mapperBuilder()
                                .enableFileOverride()
                                .enableMapperAnnotation()
                                .formatMapperFileName("%sMapper")
                                .formatXmlFileName("%sMapper");
                    })
                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();

            System.out.println("\n✅ 生成器执行完成！");
            System.out.println("📁 Entity路径: " + outputPath + "/cn/gdsdxy/campustrading/common/entity");
            System.out.println("📁 Mapper路径: " + outputPath + "/cn/gdsdxy/campustrading/common/mapper");
            System.out.println("📁 XML路径: " + xmlOutputPath);

        } catch (Exception e) {
            System.err.println("\n❌ 生成器执行失败！");
            e.printStackTrace();
        }
    }
}