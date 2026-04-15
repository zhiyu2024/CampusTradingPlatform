package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.CartEntity;
import cn.gdsdxy.campustrading.common.entity.ProductImagesEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.CartMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductImagesMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageCartUpdateParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.CartVo;
import cn.gdsdxy.campustrading.common.service.ICartService;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, CartEntity> implements ICartService {

    @Autowired
    private ProductImagesMapper productImagesMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductsMapper productsMapper;

    @Override
    public void addToCart(Integer productId) {
        Long userId = SecurityUtil.getUserId();
        Integer userIdInt = userId.intValue();

        ProductsEntity product = productsMapper.selectById(productId);
        if (product == null) throw new BusinessException(2001, "商品不存在");
        if (product.getStatus() == 0) throw new BusinessException(2002, "商品已下架");
        if (product.getStock() <= 0) throw new BusinessException(2003, "库存不足");

        long count = lambdaQuery()
                .eq(CartEntity::getUserId, userIdInt)
                .eq(CartEntity::getProductId, productId)
                .count();
        if (count > 0) throw new BusinessException(2004, "商品已在购物车中");

        CartEntity cart = new CartEntity();
        cart.setUserId(userIdInt);
        cart.setProductId(productId);
        cart.setQuantity(1);
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        cart.setCreatedAt(currentDate);
        cartMapper.insert(cart);
    }

    // ====================== 🔥 完全修复的购物车列表 ======================
    @Override
    public List<CartVo> getCartList() {
        Long userId = SecurityUtil.getUserId();
        Integer userIdInt = userId.intValue(); // 🔥 统一类型

        List<CartEntity> cartList = lambdaQuery()
                .eq(CartEntity::getUserId, userIdInt) // 🔥 修复：类型匹配
                .orderByDesc(CartEntity::getCreatedAt)
                .list();

        if (cartList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> productIds = cartList.stream().map(CartEntity::getProductId).collect(Collectors.toList());
        Map<Integer, ProductsEntity> productMap = productsMapper.selectList(
                new LambdaQueryWrapper<ProductsEntity>().in(ProductsEntity::getProductId, productIds)
        ).stream().collect(Collectors.toMap(ProductsEntity::getProductId, p -> p));

        // 🔥 修复：这里会自动剔除【已下架 / 不存在】的商品，不会报错！
        cartList = cartList.stream().filter(cart -> {
            ProductsEntity p = productMap.get(cart.getProductId());
            return p != null && p.getStatus() == 1; // 只保留正常商品
        }).collect(Collectors.toList());

        Map<Integer, List<String>> imageMap = productImagesMapper.selectList(
                new LambdaQueryWrapper<ProductImagesEntity>()
                        .in(ProductImagesEntity::getProductId, productIds)
                        .orderByAsc(ProductImagesEntity::getSortOrder)
        ).stream().collect(Collectors.groupingBy(
                ProductImagesEntity::getProductId,
                Collectors.mapping(ProductImagesEntity::getImageUrl, Collectors.toList())
        ));

        return cartList.stream().map(cart -> {
            ProductsEntity product = productMap.get(cart.getProductId());
            CartVo vo = new CartVo();
            vo.setCartId(cart.getCartId());
            vo.setProductId(product.getProductId());
            vo.setProductName(product.getProductName());
            vo.setPrice(product.getPrice());
            vo.setQuantity(cart.getQuantity());
            vo.setIsBargainable(product.getIsBargainable() ? 1 : 0);
            vo.setStock((int) product.getStock());
            vo.setCreatedAt(DateUtil.toLocalDateTime(cart.getCreatedAt()));
            vo.setProductImage(imageMap.getOrDefault(product.getProductId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(MessageCartUpdateParam param) {
        Long userId = SecurityUtil.getUserId();
        Integer userIdInt = userId.intValue();

        CartEntity cart = cartMapper.selectById(param.getCartId());
        if (cart == null || !cart.getUserId().equals(userIdInt)) {
            throw new BusinessException(2005, "购物车项不存在");
        }

        ProductsEntity product = productsMapper.selectById(cart.getProductId());
        if (product == null) {
            throw new BusinessException(2001, "商品不存在");
        }
        // 🔥 修复：结算时自动检查商品状态
        if (product.getStatus() == 0) {
            throw new BusinessException(2002, "商品已下架");
        }
        if (param.getQuantity() > product.getStock()) {
            throw new BusinessException(2006, "库存不足，最多购买" + product.getStock() + "件");
        }

        boolean updateNum = lambdaUpdate()
                .eq(CartEntity::getCartId, param.getCartId())
                .set(CartEntity::getQuantity, param.getQuantity())
                .update();

        if (!updateNum) {
            throw new BusinessException(391, "购物车不存在或数量更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromCart(Integer cartId) {
        Long userId = SecurityUtil.getUserId();
        Integer userIdInt = userId.intValue();

        boolean removed = lambdaUpdate()
                .eq(CartEntity::getCartId, cartId)
                .eq(CartEntity::getUserId, userIdInt)
                .remove();

        if (!removed) {
            throw new BusinessException(392, "您没有权限操作该购物车，或购物车不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart() {
        Long userId = SecurityUtil.getUserId();
        Integer userIdInt = userId.intValue();

        long count = lambdaQuery().eq(CartEntity::getUserId, userIdInt).count();
        if (count > 0) {
            lambdaUpdate().eq(CartEntity::getUserId, userIdInt).remove();
        }
    }
}