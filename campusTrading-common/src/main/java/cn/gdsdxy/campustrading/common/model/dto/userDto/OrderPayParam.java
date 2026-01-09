package cn.gdsdxy.campustrading.common.model.dto.userDto;

import lombok.Data;

@Data
public class OrderPayParam {
    private Integer orderId;
    private Integer payMethod;
}