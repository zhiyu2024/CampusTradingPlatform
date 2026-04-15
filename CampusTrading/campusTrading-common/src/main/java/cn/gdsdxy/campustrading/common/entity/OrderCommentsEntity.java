package cn.gdsdxy.campustrading.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_comments")
public class OrderCommentsEntity {

    @TableId(type = IdType.AUTO)
    private Integer commentId;

    private Integer orderId;
    private String orderNo;
    private Integer productId;
    private Integer buyerId;
    private Integer sellerId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;

    private Integer sentiment;
    private java.math.BigDecimal confidence;

    // 买家昵称（数据库不存在，仅用于返回前端）
    @TableField(exist = false)
    private String buyerNickname;

    // 买家头像（数据库不存在，仅用于返回前端）
    @TableField(exist = false)
    private String buyerAvatar;

// GET SET 方法（你用lombok就不用写，否则自动生成）
}