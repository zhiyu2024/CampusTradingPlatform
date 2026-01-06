package cn.gdsdxy.campustrading.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Getter
@Setter
@ToString
@TableName("orders")
public class OrdersEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商品ID
     */
    private Integer productId;

    /**
     * 买家ID
     */
    private Integer buyerId;

    /**
     * 卖家ID
     */
    private Integer sellerId;

    /**
     * 0-待付款, 1-已完成, 2-已取消
     */
    private Byte status;

    /**
     * 订单金额
     */
    private BigDecimal totalAmount;

    /**
     * 买家留言（替代单独消息表）
     */
    private String buyerNote;

    private Date createdAt;

    private Date updatedAt;
}
