package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.CartMapper;
import cn.gdsdxy.campustrading.common.mapper.OrdersMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderCreateParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderPayParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.OrderDetailVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.OrderListVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.PageVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IOrdersService;
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    UsersMapper usersMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createOrder(OrderCreateParam param) {
        Long userId = SecurityUtil.getUserId();
        Integer buyerId = userId.intValue();

        // 1. 校验商品
        ProductsEntity product = productsMapper.selectById(param.getProductId());
        if (product == null) throw new BusinessException(3001, "商品不存在");
        if (product.getStatus() != 1) throw new BusinessException(3002, "商品已下架");
        if (product.getStock() < param.getQuantity()) {
            throw new BusinessException(3003, "库存不足，仅剩" + product.getStock() + "件");
        }

        // 2. 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis() + RandomUtil.randomNumbers(5);

        // 3. 计算总金额
        BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(param.getQuantity()));

        // 4. 创建订单
        OrdersEntity order = new OrdersEntity();
        order.setOrderNo(orderNo);
        order.setProductId(param.getProductId());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setStatus((byte)0); // 待付款
        order.setTotalAmount(totalAmount);
        order.setBuyerNote(param.getBuyerNote());
        order.setQuantity(param.getQuantity());
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        ordersMapper.insert(order);

        return order.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(OrderPayParam param) {
        Long userId = SecurityUtil.getUserId();

        // 1. 校验订单
        OrdersEntity order = ordersMapper.selectById(param.getOrderId());
        if (order == null || !order.getBuyerId().equals(userId.intValue())) {
            throw new BusinessException(3004, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(3005, "订单状态异常，无法支付");
        }

        // 2. 模拟支付
        // TODO: 调用支付SDK

        // 3. 更新状态
        order.setStatus((byte)1); // 已完成
        order.setUpdatedAt(new Date());
        ordersMapper.updateById(order);
    }

    @Override
    public OrderDetailVo getOrderDetail(Integer orderId) {
        Long userId = SecurityUtil.getUserId();

        // 1. 查询订单
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) throw new BusinessException(3004, "订单不存在");
        if (!order.getBuyerId().equals(userId.intValue()) && !order.getSellerId().equals(userId.intValue())) {
            throw new BusinessException(3006, "无权查看此订单");
        }

        // 2. 查询商品信息
        ProductsEntity product = productsMapper.selectById(order.getProductId());

        // 3. 查询卖家信息
        UsersEntity seller = usersMapper.selectById(order.getSellerId());

        // 4. 组装VO（无图片）
        OrderDetailVo vo = new OrderDetailVo();
        BeanUtil.copyProperties(order, vo);
        vo.setProductName(product != null ? product.getProductName() : null);
        vo.setPrice(product != null ? product.getPrice() : null);
        vo.setCreatedAt(DateUtil.toLocalDateTime(order.getCreatedAt()));
        vo.setSellerNickname(seller != null ? seller.getNickname() : null);
        return vo;
    }

    @Override
    public IPage<OrderListVo> getBuyerOrderList(Integer status, Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtil.getUserId();
        Page<OrdersEntity> page = new Page<>(pageNum, pageSize);

        // 查询买家订单分页
        LambdaQueryWrapper<OrdersEntity> wrapper = new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getBuyerId, userId.intValue())
                .eq(status != null, OrdersEntity::getStatus, status)
                .orderByDesc(OrdersEntity::getCreatedAt);

        IPage<OrdersEntity> entityPage = ordersMapper.selectPage(page, wrapper);

        // 转换为VO（逐个查询商品和对方用户信息）
        return entityPage.convert(order -> {
            OrderListVo vo = new OrderListVo();
            BeanUtil.copyProperties(order, vo);
            vo.setCreatedAt(DateUtil.toLocalDateTime(order.getCreatedAt()));

            // 查询商品名称
            ProductsEntity product = productsMapper.selectById(order.getProductId());
            vo.setProductName(product != null ? product.getProductName() : null);

            // 查询卖家信息（买家视角）
            UsersEntity seller = usersMapper.selectById(order.getSellerId());
            vo.setOppositeId(seller != null ? seller.getUserId().intValue() : null);
            vo.setOppositeName(seller != null ? seller.getNickname() : null);

            return vo;
        });
    }

    @Override
    public IPage<OrderListVo> getSellerOrderList(Integer status, Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtil.getUserId();
        Page<OrdersEntity> page = new Page<>(pageNum, pageSize);

        // 查询卖家订单分页
        LambdaQueryWrapper<OrdersEntity> wrapper = new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getSellerId, userId.intValue())
                .eq(status != null, OrdersEntity::getStatus, status)
                .orderByDesc(OrdersEntity::getCreatedAt);

        IPage<OrdersEntity> entityPage = ordersMapper.selectPage(page, wrapper);

        // 转换为VO（逐个查询商品和对方用户信息）
        return entityPage.convert(order -> {
            OrderListVo vo = new OrderListVo();
            BeanUtil.copyProperties(order, vo);
            vo.setCreatedAt(DateUtil.toLocalDateTime(order.getCreatedAt()));

            // 查询商品名称
            ProductsEntity product = productsMapper.selectById(order.getProductId());
            vo.setProductName(product != null ? product.getProductName() : null);

            // 查询买家信息（卖家视角）
            UsersEntity buyer = usersMapper.selectById(order.getBuyerId());
            vo.setOppositeId(buyer != null ? buyer.getUserId().intValue() : null);
            vo.setOppositeName(buyer != null ? buyer.getNickname() : null);

            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Integer orderId) {
        Long userId = SecurityUtil.getUserId();

        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) throw new BusinessException(3004, "订单不存在");
        if (!order.getBuyerId().equals(userId.intValue())) {
            throw new BusinessException(3006, "无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(3007, "只有待付款订单可取消");
        }

        order.setStatus((byte)2); // 已取消
        order.setUpdatedAt(new Date());
        ordersMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(Integer orderId) {
        Long userId = SecurityUtil.getUserId();

        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) throw new BusinessException(3004, "订单不存在");
        if (!order.getBuyerId().equals(userId.intValue())) {
            throw new BusinessException(3006, "无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(3008, "只有已完成订单可确认收货");
        }

        order.setUpdatedAt(new Date());
        ordersMapper.updateById(order);
    }
}
