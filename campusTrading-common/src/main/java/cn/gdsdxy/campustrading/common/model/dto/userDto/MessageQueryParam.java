package cn.gdsdxy.campustrading.common.model.dto.userDto;

import lombok.Data;

@Data
public class MessageQueryParam {//聊天记录查询表
    private Integer productId;

    private Integer otherUserId;
}
