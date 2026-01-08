package cn.gdsdxy.campustrading.common.model.dto.aDto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private String productName;        // 商品名称
    private String description;        // 详细描述
    private BigDecimal price;          // 价格
    private Integer categoryId;        // 分类ID
//    private Integer sellerId;          // 卖家ID
    private Integer isBargainable;     // 是否支持砍价
    private BigDecimal discountRate;   // 折扣率
    private List<MultipartFile> imageUrl; // 图片文件列表
}
