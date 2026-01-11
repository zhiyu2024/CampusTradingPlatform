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
    @Select("SELECT c.category_name, SUM(o.total_amount) AS total_sales " +
            "FROM orders o " +
            "LEFT JOIN products p ON o.product_id = p.product_id " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +  // ✅ 修改为 categories
            "WHERE o.status = 1 " +
            "GROUP BY c.category_name")
    List<Map<String, Object>> selectCategorySalesStats();
}