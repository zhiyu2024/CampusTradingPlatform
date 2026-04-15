package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 管理员商品列表VO
 */
@Data
public class ProductListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String productName;
    private String description;
    private BigDecimal price;

    // 分类信息
    private Integer categoryId;
    private String categoryName;

    // 卖家信息
    private Integer sellerId;
    private String sellerNickname;

    // 库存
    private Byte stock;

    // 状态: 1-在售, 2-已售出, 3-下架
    private Byte status;

    // 浏览量
    private Integer viewCount;

    // 是否支持砍价: 0-不支持, 1-支持
    private Boolean isBargainable;

    // 折扣率
    private BigDecimal discountRate;

    // 商品图片
    private List<ProductImageVo> images;

    private Date createdAt;
    private Date updatedAt;

    // 审核状态: 0-待审核, 1-已通过, 2-疑似违规, 3-已拒绝
    private Byte auditStatus;

    // 审核结果/原因
    private String auditResult;
}
