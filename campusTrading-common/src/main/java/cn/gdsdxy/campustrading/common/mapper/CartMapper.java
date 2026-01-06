package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.CartEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<CartEntity> {

    @Insert({
            "<script>",
            "INSERT INTO cart (user_id, product_id, created_at) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.userId}, #{item.productId}, #{item.createdAt})",
            "</foreach>",
            "</script>"
    })
    void batchInsert(@Param("list") List<CartEntity> list);
}