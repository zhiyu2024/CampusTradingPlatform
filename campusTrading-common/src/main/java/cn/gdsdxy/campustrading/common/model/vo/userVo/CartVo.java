package cn.gdsdxy.campustrading.common.model.vo.userVo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartVo {//前端传过来的是商品Id  所以  不需要后端创建param接受请求体
    private String productName;
    private List<String> productImage;
    private BigDecimal price;
    private Integer quantity;
    private Integer isBargainable;
    private Integer stock;
    private LocalDateTime createdAt;
}