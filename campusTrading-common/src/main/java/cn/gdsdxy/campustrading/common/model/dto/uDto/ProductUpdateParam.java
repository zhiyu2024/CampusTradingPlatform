package cn.gdsdxy.campustrading.common.model.dto.uDto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateParam {
    private Long productId;           // 商品ID（必须）
    private String productName;          // 商品名称
    private String description;          // 描述
    private BigDecimal price;            // 价格
    private Integer categoryId;          // 分类ID
    private Integer isBargainable;       // 是否支持砍价
    private BigDecimal discountRate;     // 折扣率
    // ✅ 图片处理
    private List<Integer> deleteImageIds; // 要删除的图片ID列表
    private List<MultipartFile> newImages; // 新上传的图片
}