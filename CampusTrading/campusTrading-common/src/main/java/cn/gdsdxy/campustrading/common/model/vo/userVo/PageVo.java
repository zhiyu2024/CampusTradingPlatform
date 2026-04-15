package cn.gdsdxy.campustrading.common.model.vo.userVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PageVo<T> {
    private Long total;      // 总记录数（例：300）
    private Long pages;      // 总页数（例：19页）
    private Long current;    // 当前页（例：1）
    private Long size;       // 每页大小（例：16）
    private List<T> records; // 当前页数据列表（ID 1-16的商品）

    private Integer pageNum;
    private Integer pageSize;
}