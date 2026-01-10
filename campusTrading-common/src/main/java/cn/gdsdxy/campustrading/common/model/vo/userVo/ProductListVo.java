package cn.gdsdxy.campustrading.common.model.vo.userVo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer productId;     // 商品ID
    private String productName;    // 商品名称
    private String description;    // 简要描述（可截断）
    private BigDecimal price;      // 价格
    private Integer categoryId;    // 分类ID
    private Boolean isBargainable; // 是否支持砍价
    private BigDecimal discountRate; // 折扣率
    private Integer viewCount;     // 浏览量
    private List<String> imageUrls; // 商品图片列表（关键优化）
    private Date createdAt;        // 创建时间
}