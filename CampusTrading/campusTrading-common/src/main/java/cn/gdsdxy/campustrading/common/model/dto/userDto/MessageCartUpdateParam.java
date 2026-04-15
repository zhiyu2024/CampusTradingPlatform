package cn.gdsdxy.campustrading.common.model.dto.userDto;


import lombok.Data;

@Data
public class MessageCartUpdateParam {//聊天记录更新表
    private Integer cartId;
    private Integer quantity;
}