package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.model.dto.userDto.CartUpdateParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.CartVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.ICartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // ✅ 添加日志
@Tag(name = "用户购物车管理", description = "用户购物车相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/user/cart")
public class UserCartController {
    @Autowired
    ICartService iCartService;
    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public FwResult<String> addToCart(@RequestParam Integer productId) {
        iCartService.addToCart(productId);
        return FwResult.ok("添加成功");
    }

    /**
     * 查看购物车列表
     */
    @GetMapping("/list")
    public FwResult<List<CartVo>> getCartList() {
        List<CartVo> list = iCartService.getCartList();
        return FwResult.ok(list);
    }

    /**
     * 修改商品数量
     */
    @PostMapping("/update")
    public FwResult<String> updateQuantity(@RequestBody CartUpdateParam param) {
        iCartService.updateQuantity(param);
        return FwResult.ok("更新成功");
    }

    /**
     * 移除购物车商品
     */
    @PostMapping("/remove")
    public FwResult<String> removeFromCart(@RequestParam Integer cartId) {
        iCartService.removeFromCart(cartId);
        return FwResult.ok("移除成功");
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public FwResult<String> clearCart() {
        iCartService.clearCart();
        return FwResult.ok("清空成功");
    }
}
