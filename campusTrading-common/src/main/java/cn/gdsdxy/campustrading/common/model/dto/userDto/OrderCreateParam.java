package cn.gdsdxy.campustrading.common.model.dto.userDto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateParam {
    private List<Integer> productIds;
    private String payType;//支付方式
}