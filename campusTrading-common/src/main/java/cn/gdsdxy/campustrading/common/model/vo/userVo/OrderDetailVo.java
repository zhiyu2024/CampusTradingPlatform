package cn.gdsdxy.campustrading.common.model.vo.userVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVo {
    private Integer orderId;
    private String orderNo;
    private Integer status;
    private BigDecimal totalAmount;
    private String buyerNote;
    private UserInfoVo buyerInfo;
    private UserInfoVo sellerInfo;
    private List<ProductVo> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}