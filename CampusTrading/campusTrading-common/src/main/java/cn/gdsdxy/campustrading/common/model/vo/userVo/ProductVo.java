package cn.gdsdxy.campustrading.common.model.vo.userVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVo {
    private Long productId;
    private String productName;
    private Boolean isBargainable; // 是否支持砍价
    private BigDecimal discountRate; // 折扣率
    private Integer viewCount;     // 浏览量
    private Long sellerId;
    private String campus;         // 校区
    private List<String> imageUrl; // 返回URL
    private String message;
}