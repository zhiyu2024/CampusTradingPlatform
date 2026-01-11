package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper extends BaseMapper<OrdersEntity> {

    // 自定义SQL：查询商品分类销售占比
    @Select("SELECT " +
            "pc.category_name, " +
            "SUM(o.total_amount) AS total_sales " +
            "FROM orders o " +
            "LEFT JOIN products p ON o.product_id = p.product_id " +
            "LEFT JOIN product_category pc ON p.category_id = pc.category_id " +
            "WHERE o.status = 1 " + // 只统计已完成订单
            "GROUP BY pc.category_name")
    List<Map<String, Object>> selectCategorySalesStats();
}