package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

import java.util.List;

@Data
public class DataBoardVo {
    // 1. 用户增长趋势数据（折线图）
    private List<UserGrowthVo> userGrowthList;

    // 2. 商品分类统计数据（饼图）
    private List<CategoryStatVo> categoryStatList;

    // 3. 订单状态分布数据（环形图）
    private List<OrderStatusStatVo> orderStatusStatList;

    // 4. 销售额趋势数据（柱状图）
    private List<SalesTrendVo> salesTrendList;
}