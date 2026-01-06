package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.CartEntity;
import cn.gdsdxy.campustrading.common.mapper.CartMapper;
import cn.gdsdxy.campustrading.common.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, CartEntity> implements ICartService {

}
