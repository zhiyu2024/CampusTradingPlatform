package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Service
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, ProductsEntity> implements IProductsService {

}
