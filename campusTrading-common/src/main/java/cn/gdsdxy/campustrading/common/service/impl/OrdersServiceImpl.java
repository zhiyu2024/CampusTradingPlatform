package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.CartMapper;
import cn.gdsdxy.campustrading.common.mapper.OrdersMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderCreateParam;
import cn.gdsdxy.campustrading.common.service.IOrdersService;
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, OrdersEntity> implements IOrdersService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入加密器

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    ProductsMapper productsMapper;
    @Autowired
    CartMapper cartMapper;
    @Override
    @Transactional
    public Integer createOrder( OrderCreateParam param) {
        Long userId= SecurityUtil.getUserId();
        // 1. 生成订单号
        String orderNo = generateOrderNo();

        // 2. 计算总金额，检查商品状态
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Integer productId : param.getProductIds()) {
            ProductsEntity product = productsMapper.selectById(productId);
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException(3001,"商品不存在或已下架");
            }
            if (product.getStock() <= 0) {
                throw new BusinessException(3002,"商品库存不足");
            }
            totalAmount = totalAmount.add(product.getPrice());
        }

        // 3. 创建订单
        OrdersEntity order = new OrdersEntity();
        order.setOrderNo(orderNo);
        order.setBuyerId(userId.intValue());
        order.setTotalAmount(totalAmount);
        order.setBuyerNote(param.getBuyerNote());
        order.setStatus(0); // 待付款
        ordersMapper.insert(order);

        // 4. TODO: 创建订单商品关联（需要创建order_items表）

        return order.getOrderId();
    }
}
