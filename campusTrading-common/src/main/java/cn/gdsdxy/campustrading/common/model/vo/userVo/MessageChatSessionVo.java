package cn.gdsdxy.campustrading.common.model.vo.userVo;


import lombok.Data;

@Data
public class MessageChatSessionVo {
    private Integer productId;
    private String productName;
    private String productImage;
    private Integer oppositeUserId;
    private String oppositeNickname;
    private String oppositeAvatar;
    private String latestMessage;
    private Integer latestMessageType;
    private Integer unreadCount;
    private Data latestMessageTime;
}
