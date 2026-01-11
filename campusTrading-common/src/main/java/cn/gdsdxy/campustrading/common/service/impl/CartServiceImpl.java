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
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Slf4j//日志记录的关键
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, CartEntity> implements ICartService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入加密器

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    ProductImagesMapper productImagesMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductsMapper productsMapper;

    @Override
    public void addToCart(Integer productId) {//加入到购物车
        Long userId = SecurityUtil.getUserId();
        Integer userIdInt = userId.intValue();

        // 1. 校验商品
        ProductsEntity product = productsMapper.selectById(productId);
        if (product == null) throw new BusinessException(2001, "商品不存在");
        if (product.getStatus() != 1) throw new BusinessException(2002, "商品已下架");
        if (product.getStock() <= 0) throw new BusinessException(2003, "库存不足");

        // 2. 校验重复添加
        long count = lambdaQuery()
                .eq(CartEntity::getUserId, userIdInt)
                .eq(CartEntity::getProductId, productId)
                .count();
        if (count > 0) throw new BusinessException(2004, "商品已在购物车中");

        // 3. 添加到购物车
        CartEntity cart = new CartEntity();
        cart.setUserId(userIdInt);
        cart.setProductId(productId);
        cart.setQuantity(1); // 默认数量1
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        cart.setCreatedAt(currentDate);
        cartMapper.insert(cart);
    }

    @Override
    public List<CartVo> getCartList() {//获取购物车列表
        Long userId = SecurityUtil.getUserId();

        // 1. 查询购物车列表
        List<CartEntity> cartList = lambdaQuery()
                .eq(CartEntity::getUserId, userId.intValue())
                .orderByDesc(CartEntity::getCreatedAt)
                .list();


        if (cartList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 收集商品ID
        List<Integer> productIds = cartList.stream()
                .map(CartEntity::getProductId)
                .collect(Collectors.toList());
        //从购物车列表中提取所有商品 ID 并封装成列表，是 Java 8 + 的 Stream 流操作，核心作用是 “数据转换 + 收集”

        // 3. 批量查询商品信息
        Map<Integer, ProductsEntity> productMap = productsMapper.selectList(
                new LambdaQueryWrapper<ProductsEntity>().in(ProductsEntity::getProductId, productIds)
        ).stream().collect(Collectors.toMap(ProductsEntity::getProductId, p -> p));
      //3.1 如果有商品的id列表在map流,查询id在数据库里保存到列表的过程中,遇到id不存在情况:校验
        List<Integer> notExistProductIds = productIds.stream()
                .filter(id -> !productMap.containsKey(id))
                .collect(Collectors.toList());

        if (!notExistProductIds.isEmpty()) {
            throw new BusinessException(2010,"商品ID：" + notExistProductIds + " 不存在或已下架");
        }

        // 4. 批量查询商品图片
        Map<Integer, List<String>> imageMap = productImagesMapper.selectList(
                new LambdaQueryWrapper<ProductImagesEntity>()
                        .in(ProductImagesEntity::getProductId, productIds)
                        .orderByAsc(ProductImagesEntity::getSortOrder)
        ).stream().collect(Collectors.groupingBy(
                ProductImagesEntity::getProductId,
                Collectors.mapping(ProductImagesEntity::getImageUrl, Collectors.toList())
        ));

        // 5. 组装VO
        return cartList.stream().map(cart -> {
            ProductsEntity product = productMap.get(cart.getProductId());
            if (product == null) return null;

            CartVo vo = new CartVo();
            vo.setCartId(cart.getCartId());
            vo.setProductId(product.getProductId());
            vo.setProductName(product.getProductName());
            vo.setPrice(product.getPrice());
            vo.setQuantity(cart.getQuantity());
            vo.setIsBargainable(product.getIsBargainable() ? 1 : 0);
            vo.setStock((int)product.getStock());
            vo.setCreatedAt(DateUtil.toLocalDateTime(cart.getCreatedAt()));
            //，而是把购物车实体（cart）中已有的createdAt字段（该字段是Date类型）转换成LocalDateTime类型，再赋值给 VO 的createdAt字段。
            vo.setProductImage(imageMap.getOrDefault(product.getProductId(), Collections.emptyList()));
            return vo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(MessageCartUpdateParam param) {
        Long userId = SecurityUtil.getUserId();

        // 1. 校验购物车项存在且属于当前用户
        CartEntity cart = cartMapper.selectById(param.getCartId());
        if (cart == null || !cart.getUserId().equals(userId.intValue())) {
            throw new BusinessException(2005, "购物车项不存在");
        }

        // 2. 校验库存是否足够
        ProductsEntity product = productsMapper.selectById(cart.getProductId());
        if (product != null && param.getQuantity() > product.getStock()) {
            throw new BusinessException(2006, "库存不足，最多购买" + product.getStock() + "件");
        }

        // 3. 更新数量
//        是 MyBatis-Plus 中 LambdaUpdateWrapper 的标准用
     boolean updataNum=   lambdaUpdate()
                .eq(CartEntity::getCartId, param.getCartId())//查询条件
                .set(CartEntity::getQuantity, param.getQuantity())//设置为传入值
                .update();//执行更新操作
        if(!updataNum){
            throw new BusinessException(391,"购物车不存在,或者数量更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromCart(Integer cartId) {
        Long userId = SecurityUtil.getUserId();

        // 确保删除的是当前用户的购物车项
        boolean affectedRows =lambdaUpdate()
                .eq(CartEntity::getCartId, cartId)//匹配条件
                .eq(CartEntity::getUserId, userId.intValue())//匹配条件
                .remove();//执行更新操作

        if (!affectedRows) {
            throw new BusinessException(392,"您没有权限操作该购物车，或购物车不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "cart", key = "#userId")  // ✅ 删除缓存
    public void clearCart() {
        Long userId = SecurityUtil.getUserId();
        // 1. 记录操作日志
        log.info("[购物车] 用户 {} 正在清空购物车", userId);
        // 先查询购物车是否有数据
        long count = lambdaQuery().eq(CartEntity::getUserId, userId.intValue()).count();
        log.debug("[购物车] 用户 {} 购物车中有 {} 件商品", userId, count);
        if (count > 0) {
            // 有数据才删除
            lambdaUpdate().eq(CartEntity::getUserId, userId.intValue()).remove();
        }
        // 4. 记录删除结果
        if (count>0) {
            log.info("[购物车] 用户 {} 成功清空 {} 件商品", userId, count);
        } else {
            log.warn("[购物车] 用户 {} 清空购物车失败，未删除任何数据", userId);
        }

    }
}

