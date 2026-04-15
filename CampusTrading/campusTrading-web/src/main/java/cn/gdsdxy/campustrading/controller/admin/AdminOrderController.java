package cn.gdsdxy.campustrading.controller.admin;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import cn.gdsdxy.campustrading.common.mapper.OrdersMapper;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.OrderListVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IOrdersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "管理员订单管理", description = "管理员订单相关接口")
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private OrdersMapper ordersMapper;

    @GetMapping
    public FwResult<IPage<OrderListVo>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String order_no,
            @RequestParam(required = false) Byte status) {
        log.info("管理员查询订单列表, page={}, pageSize={}, order_no={}, status={}", page, pageSize, order_no, status);
        Page<OrderListVo> pageParam = new Page<>(page, pageSize);
        IPage<OrderListVo> result = ordersMapper.selectOrderListWithDetails(pageParam, order_no, status);
        return FwResult.ok(result);
    }

    @GetMapping("/{id}")
    public FwResult<OrdersEntity> getDetail(@PathVariable Integer id) {
        log.info("管理员查询订单详情, id={}", id);
        OrdersEntity order = ordersService.getById(id);
        if (order == null) {
            return FwResult.fail("订单不存在");
        }
        return FwResult.ok(order);
    }

    @PutMapping("/{id}/status")
    public FwResult<String> updateStatus(@PathVariable Integer id, @RequestBody OrdersEntity param) {
        log.info("管理员修改订单状态, id={}, status={}", id, param.getStatus());
        OrdersEntity order = new OrdersEntity();
        order.setOrderId(id);
        order.setStatus(param.getStatus());
        ordersService.updateById(order);
        return FwResult.ok("状态更新成功");
    }

    @GetMapping("/stats")
    public FwResult<Map<String, Object>> getOrderStats() {
        log.info("管理员查询订单统计");
        Long totalOrders = ordersService.count();
        Long pendingOrders = ordersService.count(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getStatus, 0));
        Long completedOrders = ordersService.count(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getStatus, 1));
        Long cancelledOrders = ordersService.count(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getStatus, 2));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelledOrders", cancelledOrders);
        return FwResult.ok(stats);
    }
}
