package cn.gdsdxy.campustrading.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户消息记录表
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Getter
@Setter
@ToString
@TableName("messages")
public class MessagesEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID主键
     */
    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId;

    /**
     * 关联商品ID（在哪件商品页面发起的沟通）
     */
    private Integer productId;

    /**
     * 发送者ID（买家或卖家）
     */
    private Integer senderId;

    /**
     * 接收者ID（另一方）
     */
    private Integer receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：1-普通留言, 2-砍价请求, 3-回复, 4-系统通知
     */
    private Byte messageType;

    /**
     * 是否已读：0-未读, 1-已读
     */
    private Byte isRead;

    /**
     * 发送时间
     */
    private Date createdAt;
}
