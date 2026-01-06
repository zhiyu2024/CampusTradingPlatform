package cn.gdsdxy.campustrading.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 商品图片
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Getter
@Setter
@ToString
@TableName("product_images")
public class ProductImagesEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "image_id", type = IdType.AUTO)
    private Integer imageId;

    /**
     * 商品ID
     */
    private Integer productId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 排序
     */
    private Integer sortOrder;
}
