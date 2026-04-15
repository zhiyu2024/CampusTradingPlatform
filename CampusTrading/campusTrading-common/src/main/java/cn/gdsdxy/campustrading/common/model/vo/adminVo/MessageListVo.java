package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员消息列表VO
 */
@Data
public class MessageListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer messageId;

    // 商品信息
    private Integer productId;
    private String productName;

    // 发送者信息
    private Integer senderId;
    private String senderNickname;

    // 接收者信息
    private Integer receiverId;
    private String receiverNickname;

    // 消息内容
    private String content;

    // 消息类型: 1-普通留言, 2-砍价请求, 3-回复, 4-系统通知
    private Byte messageType;

    // 是否已读: 0-未读, 1-已读
    private Byte isRead;

    private Date createdAt;
}
