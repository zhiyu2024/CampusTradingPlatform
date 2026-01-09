package cn.gdsdxy.campustrading.common.model.vo.userVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderListVo {
    private Integer orderId;
    private String orderNo;
    private String productName;
    private List<String> productImage;
    private Integer status;
    private BigDecimal totalAmount;
    private String oppositeNickname;//对方用户名称 /卖家昵称
    private LocalDateTime createdAt;
}