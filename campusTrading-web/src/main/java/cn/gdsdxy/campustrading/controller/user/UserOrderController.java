package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderCreateParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderPayParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.OrderDetailVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.OrderListVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.PageVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IOrdersService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j // ✅ 添加日志
@Tag(name = "用户订单管理", description = "用户订单的相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/user/order")
public class UserOrderController {
     @Autowired
    IOrdersService iOrdersService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public FwResult<Integer> createOrder(@RequestBody OrderCreateParam param) {
        Integer orderId = iOrdersService.createOrder(param);
        return FwResult.ok(orderId);
    }

    /**
     * 订单支付
     */
    @PostMapping("/pay")
    public FwResult<String> payOrder(@RequestBody OrderPayParam param) {
        iOrdersService.payOrder( param);
        return FwResult.ok("支付成功");
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail")
    public FwResult<OrderDetailVo> getOrderDetail(@RequestParam Integer orderId) {
        OrderDetailVo detail = iOrdersService.getOrderDetail(orderId);
        return FwResult.ok(detail);
    }

    /**
     * 订单列表（买家视角）
     */
    @GetMapping("/buyer/list")
    public FwResult<IPage<OrderListVo>> getBuyerOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "16") Integer pageSize) {
        return FwResult.ok(iOrdersService.getBuyerOrderList(status, pageNum, pageSize));
    }
    /**
     * 订单列表（卖家视角）
     */
    @GetMapping("/seller/list")
    public FwResult<IPage<OrderListVo>> getSellerOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "16") Integer pageSize) {
        return FwResult.ok(iOrdersService.getSellerOrderList(status, pageNum, pageSize));
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    public FwResult<String> cancelOrder(@RequestParam Integer orderId) {
        iOrdersService.cancelOrder(orderId);
        return FwResult.ok("取消成功");
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm")
    public FwResult<String> confirmReceipt(@RequestParam Integer orderId) {
        iOrdersService.confirmReceipt(orderId);
        return FwResult.ok("确认收货成功");
    }
}
