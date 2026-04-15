package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesTrendVo {
    private String month;        // 月份（如"1月"）
    private BigDecimal salesAmount; // 对应月份的总销售额
}