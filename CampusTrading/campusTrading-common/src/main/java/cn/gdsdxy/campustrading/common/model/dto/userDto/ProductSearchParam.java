package cn.gdsdxy.campustrading.common.model.dto.userDto;

import lombok.Data;

@Data
public class ProductSearchParam {
    private String keyword;//关键词
    private Integer categoryId;//分类ID
    private Boolean isBargainable;//是否支持砍价
    private Boolean hasDiscount;//是否有折扣
    private Integer pageNum = 1;//页码
//    @ApiModelProperty(value = "最低价格")
//    private Double minPrice;//最低价格
//
//    @ApiModelProperty(value = "最高价格")
//    private Double maxPrice;//
//
//    @ApiModelProperty(value = "排序方式", notes = "1-最新发布, 2-价格从低到高, 3-价格从高到低, 4-浏览量")
//    private Integer sortType;


}