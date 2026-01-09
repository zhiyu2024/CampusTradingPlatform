package cn.gdsdxy.campustrading.common.service;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderCreateParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderPayParam;
import cn.gdsdxy.campustrading.common.result.FwResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
public interface IOrdersService extends IService<OrdersEntity> {
// 原有代码基础上添加

    /**
     * 创建订单
     */
    Integer createOrder(Integer userId, OrderCreateParam param);//返回Integer的含义：大概率是返回订单的数据库自增主键 ID（订单表的order_id，Integer 类型）—— 这是最常见的设计，因为创建订单后，前端需要这个
    // —— 这是最常见的设计，因为创建订单后，前端需要这个自增的主键订单ID 来调用getOrderDetail、payOrder等后续接口。

    /**
     * 订单支付
     */
    FwResult payOrder(Integer userId, OrderPayParam param);

    /**
     * 获取订单详情
     */
    OrderDetailVo getOrderDetail(Integer userId, Integer orderId);

    /**
     * 订单列表（买家视角）
     */
    PageVo<OrderListVo> getBuyerOrderList(Integer userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 订单列表（卖家视角）
     */
    PageVo<OrderListVo> getSellerOrderList(Integer userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 取消订单
     */
    void cancelOrder(Integer userId, Integer orderId);

    /**
     * 确认收货
     */
    void confirmReceipt(Integer userId, Integer orderId);
}
