package cn.gdsdxy.campustrading.common.model.vo.userVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderListVo {
    private Integer orderId;
    private String orderNo;
    private Integer productId;
    private String productName;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createdAt;
    private Integer oppositeId; // 对方ID（买家看是卖家ID，卖家看是买家ID）
    private String oppositeName; // 对方名称
}