package cn.gdsdxy.campustrading.common.model.dto.userDto;


import lombok.Data;

@Data
public class CartUpdateParam {
    private Integer cartId;
    private Integer quantity;
}