package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.ProductImagesEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductImagesMapper extends BaseMapper<ProductImagesEntity> {

    @Insert({
            "<script>",
            "INSERT INTO product_images (product_id, image_url, sort_order) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.productId}, #{item.imageUrl}, #{item.sortOrder})",
            "</foreach>",
            "</script>"
    })
    void batchInsert(@Param("list") List<ProductImagesEntity> list);
}