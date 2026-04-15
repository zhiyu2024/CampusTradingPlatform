package cn.gdsdxy.campustrading.common.model.dto.userDto;

import lombok.Data;

@Data
public class MessageSendParam {

    private Integer productId;
    private Integer receiverId;//接受者Id
    private String content;//消息内容不能为空
    private Integer messageType = 1;//value = "消息类型", notes = "1-普通留言, 2-砍价请求"
}