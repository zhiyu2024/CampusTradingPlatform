package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 管理员订单列表VO
 */
@Data
public class OrderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;
    private String orderNo;
    private Integer quantity;

    // 商品信息
    private Integer productId;
    private String productName;

    // 买家信息
    private Integer buyerId;
    private String buyerNickname;

    // 卖家信息
    private Integer sellerId;
    private String sellerNickname;

    // 订单状态: 0-待付款, 1-已完成, 2-已取消
    private Byte status;

    // 订单金额
    private BigDecimal totalAmount;

    // 买家留言
    private String buyerNote;

    private Date createdAt;
    private Date updatedAt;
}
