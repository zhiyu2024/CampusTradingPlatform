package cn.gdsdxy.campustrading.common.model.vo.userVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVo {
    private Long productId;
    private String productName;
    private Long sellerId;
    private List<String> imageUrl; // 返回URL
    private String message;
}