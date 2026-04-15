package cn.gdsdxy.campustrading.common.model.vo.adminVo;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVo {//数据概览VO
    private Long totalUsers;//用户总数
    private Long totalProducts;//商品总数
    private Long totalOrders;//订单总数
    private BigDecimal totalTransactionAmount;//总交易额
    private List<Map<String, Object>> categorySalesStats;//商品分类销售占比
    private List<Map<String, Object>> weeklyTrend;//最近7天趋势数据
//    private Long todayNewUsers;//今日新增用户
//    private Long todayNewProducts;//今日新增商品
//    private Long todayOrders;//今日订单数
//    private BigDecimal todayTransactionAmount;//今日交易额

}