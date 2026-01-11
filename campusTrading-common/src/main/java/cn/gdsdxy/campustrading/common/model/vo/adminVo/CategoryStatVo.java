package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

@Data
public class CategoryStatVo {
    private String categoryName; // 分类名称（如"教材资料"）
    private Long productCount;   // 对应分类的商品数量
}