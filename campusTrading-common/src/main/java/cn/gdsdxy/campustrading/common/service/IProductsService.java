package cn.gdsdxy.campustrading.common.service;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.model.dto.userDto.ProductDto;
import cn.gdsdxy.campustrading.common.model.dto.userDto.ProductSearchParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.ProductUpdateParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.PageVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductDetailVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductListVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
public interface IProductsService extends IService<ProductsEntity> {
    ProductVo AddProduct(ProductDto productDto);
    IPage<ProductListVo> selectProductPage(Integer pageNum, Integer pageSize);
    ProductVo updateProduct(ProductUpdateParam productUpdateParam);
    FwResult deleteByProduct(Integer productId );
    ProductDetailVo getProductDetail(Integer productId);
    /**
     * 搜索商品
     */
    IPage<ProductListVo> searchProducts(Integer pageNum, Integer pageSize,ProductSearchParam param);

}
