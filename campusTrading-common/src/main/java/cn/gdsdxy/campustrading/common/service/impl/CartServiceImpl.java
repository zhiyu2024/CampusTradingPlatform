package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.CartEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.CartMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.vo.userVo.CartVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.ICartService;
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入加密器

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductsMapper productsMapper;
    @Override
    public FwResult addToCart(Integer productId) {
        Long userId= SecurityUtil.getUserId();
        // 检查商品是否存在且在售
        ProductsEntity product = productsMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(2001,"商品不存在或已下架");
        }

        // 检查库存
        if (product.getStock() <= 0) {
            throw new BusinessException(2002,"商品库存不足");
        }

        // 检查购物车是否已存在
        LambdaQueryWrapper<CartEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartEntity::getUserId, userId).eq(CartEntity::getProductId, productId);
        CartEntity existing = cartMapper.selectOne(wrapper);

        if (existing != null) {
            throw new BusinessException(2003,"商品已在购物车中");
        }

        // 添加到购物车
        CartEntity cart = new CartEntity();
        cart.setUserId(userId.intValue());
        cart.setProductId(productId);
        cartMapper.insert(cart);
        return FwResult.ok("商品添加购物车成功");
    }

    @Override
    public List<CartVo> getCartList(Integer userId) {
        return cartMapper.getCartDetailList(userId);
    }
}
