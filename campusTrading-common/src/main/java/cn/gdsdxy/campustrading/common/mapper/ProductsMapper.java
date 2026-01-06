package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductsMapper extends BaseMapper<ProductsEntity> {

    @Insert({
            "<script>",
            "INSERT INTO products (product_name, description, price, category_id, seller_id, stock, status, view_count, is_bargainable, discount_rate, created_at, updated_at) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.productName}, #{item.description}, #{item.price}, #{item.categoryId}, #{item.sellerId}, #{item.stock}, #{item.status}, #{item.viewCount}, #{item.isBargainable}, #{item.discountRate}, #{item.createdAt}, #{item.updatedAt})",
            "</foreach>",
            "</script>"
    })
    void batchInsert(@Param("list") List<ProductsEntity> list);
}