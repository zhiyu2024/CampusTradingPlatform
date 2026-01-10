package cn.gdsdxy.campustrading.common.model.dto.userDto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateParam {
    private Integer productId;

    private Integer quantity;

    private String buyerNote; // 买家留言
    private List<Integer> productIds;
}