package cn.gdsdxy.campustrading.common.service;

import cn.gdsdxy.campustrading.common.entity.CartEntity;
import cn.gdsdxy.campustrading.common.model.dto.userDto.CartUpdateParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.CartVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
public interface ICartService extends IService<CartEntity> {
    /**
     * 加入购物车
     */
    void addToCart(Integer productId);//前端传商品Id进来,然后解析请求头Token 获取userId 加入购物车的User_id

    /**
     * 查看购物车列表
     */
    List<CartVo> getCartList( );//解析请求头携带的token 然后获取token里面的userId 就可以获取到该用户的购物车列表

    /**
     * 修改商品数量
     */
    void updateQuantity( CartUpdateParam param);//解析请求头携带的token 然后获取token里面的userId
  //修改该用户商品数量
    /**
     * 移除购物车商品
     */
    void removeFromCart( Integer cartId);//解析请求头携带的token 然后获取token里面的userId
    //移除该用户的购物车商品
    /**
     * 清空购物车
     */
    void clearCart();
    //清空该用户的购物车商品
}
