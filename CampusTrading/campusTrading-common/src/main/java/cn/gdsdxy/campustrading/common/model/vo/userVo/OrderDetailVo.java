package cn.gdsdxy.campustrading.common.model.vo.userVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVo {
    private Integer orderId;
    private String orderNo;
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private Integer status;
    private BigDecimal totalAmount;
    private String buyerNote;
    private LocalDateTime createdAt;
    private Integer sellerId;
    private String sellerNickname; // 卖家昵称（需关联用户表）
}