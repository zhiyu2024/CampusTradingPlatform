package cn.gdsdxy.campustrading.common.model.vo.userVo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageVo {
    private Integer senderId;//发送者Id
    private String senderNickname;//发送人昵称
    private String senderAvatar;//头像url
    private Integer receiverNickname;//接收人昵称
    private String receiverAvatar;//头像url
    private String content;//信息内容
    private Integer messageType;//消息类型
    private Integer isRead;//是否已读
    private LocalDateTime createdAt;
}