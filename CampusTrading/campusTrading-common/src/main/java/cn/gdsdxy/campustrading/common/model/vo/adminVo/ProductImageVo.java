package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品图片VO
 */
@Data
public class ProductImageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer imageId;
    private Integer productId;
    private String imageUrl;
    private Integer sortOrder;
}
