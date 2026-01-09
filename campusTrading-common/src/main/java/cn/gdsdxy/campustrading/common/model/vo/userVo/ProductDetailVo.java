package cn.gdsdxy.campustrading.common.model.vo.userVo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetailVo {
    private Integer productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private BigDecimal discountRate;
    private BigDecimal discountedPrice;
    private Integer categoryId;
    private String categoryName;
    private Integer sellerId;
    private String sellerNickname;
    private String sellerAvatar;
    private String sellerCampus;
    private Integer isBargainable;
    private Integer stock;
    private Integer status;
    private Integer viewCount;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}