package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrdersMapper extends BaseMapper<OrdersEntity> {

    @Insert({
            "<script>",
            "INSERT INTO orders (order_no, product_id, buyer_id, seller_id, status, total_amount, buyer_note, created_at, updated_at) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.orderNo}, #{item.productId}, #{item.buyerId}, #{item.sellerId}, #{item.status}, #{item.totalAmount}, #{item.buyerNote}, #{item.createdAt}, #{item.updatedAt})",
            "</foreach>",
            "</script>"
    })
    void batchInsert(@Param("list") List<OrdersEntity> list);
}