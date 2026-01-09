package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductDetailVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductsMapper extends BaseMapper<ProductsEntity> {

    /**
     * 查询商品详情（包含卖家信息）
     */
    ProductDetailVo selectProductDetailWithSeller(@Param("productId") Integer productId);

    /**
     * 查询商品图片列表
     */
    List<String> selectProductImages(@Param("productId") Integer productId);

    /**
     * 增加浏览量
     */
    void incrementViewCount(@Param("productId") Integer productId);
}