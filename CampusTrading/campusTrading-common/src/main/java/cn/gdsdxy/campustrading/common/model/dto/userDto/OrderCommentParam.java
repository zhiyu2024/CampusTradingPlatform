package cn.gdsdxy.campustrading.common.model.dto.userDto;


import lombok.Data;


@Data
public class OrderCommentParam {


    private Integer orderId;


    private Integer productId;

    private String content;

    private Integer score = 5;
}