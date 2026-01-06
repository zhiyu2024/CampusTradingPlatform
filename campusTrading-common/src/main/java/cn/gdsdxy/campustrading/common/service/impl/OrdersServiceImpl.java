package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import cn.gdsdxy.campustrading.common.mapper.OrdersMapper;
import cn.gdsdxy.campustrading.common.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
