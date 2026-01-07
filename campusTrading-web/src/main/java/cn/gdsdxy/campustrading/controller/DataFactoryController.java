//package cn.gdsdxy.campustrading.controller;
//
//
//import cn.gdsdxy.campustrading.common.factory.AllDataFactory;
//import org.slf4j.Logger;  // ✅ 正确：slf4j.Logger
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//@RestController
//@RequestMapping("/api/data-factory")
//public class DataFactoryController {
//
//    @Autowired
//    private AllDataFactory allDataFactory;
//
//    @GetMapping(value = "/init-all", produces = "application/json;charset=UTF-8")//, produces = "application/json;charset=UTF-8"
//    public String initAllData() {
//        try {
//            allDataFactory.generateAllData();
//            return "✅ 数据生成任务已启动，查看控制台日志";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "❌ 失败: " + e.getMessage();
//        }
//    }
//}