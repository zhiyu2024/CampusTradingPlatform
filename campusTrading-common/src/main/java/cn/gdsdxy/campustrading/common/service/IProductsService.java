package cn.gdsdxy.campustrading.common.service;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.model.dto.aDto.ProductDto;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductVo;
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
}
