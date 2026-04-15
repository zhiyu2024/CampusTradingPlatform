package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

@Data
public class OrderStatusStatVo {
    private String statusName; // 订单状态名（如"待付款"）
    private Long orderCount;   // 对应状态的订单数量
}