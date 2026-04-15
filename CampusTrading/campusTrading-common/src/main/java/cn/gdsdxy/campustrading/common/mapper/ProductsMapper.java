package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.ProductImageVo;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.ProductListVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductDetailVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 管理员查询商品列表（包含关联信息）
     */
    IPage<ProductListVo> selectProductListWithDetails(Page<ProductListVo> page,
                                                       @Param("productName") String productName,
                                                       @Param("status") Byte status,
                                                       @Param("categoryId") Integer categoryId,
                                                       @Param("auditStatus") Byte auditStatus);

    /**
     * 查询商品图片列表（用于列表展示）
     */
    List<ProductImageVo> selectProductImagesForList(@Param("productId") Integer productId);
}