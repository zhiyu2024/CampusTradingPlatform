package cn.gdsdxy.campustrading.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商品信息
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Data
@ToString
@TableName("products")
public class ProductsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "product_id", type = IdType.AUTO)
    private Integer productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 卖家ID
     */
    private Integer sellerId;

    /**
     * 库存（二手默认为1）
     */
    private Byte stock;

    /**
     * 1-在售, 2-已售出, 3-下架
     */
    private Byte status;

    /**
     * 浏览量
     */
    private Integer viewCount;

    private Date createdAt;

    private Date updatedAt;

    /**
     * 是否支持砍价：0-不支持，1-支持
     */
    private Boolean isBargainable;

    /**
     * 折扣率（0.00-1.00，如0.85表示85折）
     */
    private BigDecimal discountRate;

    /**
     * AI生成的商品描述（历史版本）
     */
    private String aiGeneratedDesc;

    /**
     * AI描述版本号，用于迭代更新
     */
    private Integer aiDescVersion;

    /**
     * 0:出售 1:以物换物 2:求购
     */
    private Byte exchangeType;

    /**
     * 想要交换的物品描述
     */
    private String exchangeWant;

    /**
     * AI匹配度评分
     */
    private java.math.BigDecimal exchangeMatchScore;

    /**
     * 0:待审核 1:已通过 2:疑似违规 3:已拒绝
     */
    private Byte auditStatus;

    /**
     * AI审核结果/违规原因
     */
    private String auditResult;
}
