package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

@Data
public class UserGrowthVo {
    private String month;      // 月份（如"1月"）
    private Long userCount;    // 对应月份的用户数
}