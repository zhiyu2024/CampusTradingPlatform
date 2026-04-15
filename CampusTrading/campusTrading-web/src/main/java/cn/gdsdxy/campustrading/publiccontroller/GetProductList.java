package cn.gdsdxy.campustrading.publiccontroller;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.model.vo.userVo.PageVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductDetailVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductListVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j // ✅ 添加日志
@Tag(name = "公共接口管理") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/public")
public class GetProductList {
    @Autowired
    private IProductsService iProductsService;
    /**
     * 获取商品详情
     */
    @GetMapping("/product/detail")
    public FwResult<ProductDetailVo> getProductDetail(@RequestParam Integer productId) {

        ProductDetailVo productDetail = iProductsService.getProductDetail(productId);
        return FwResult.ok(productDetail);
    }

    @GetMapping("/product")
    public FwResult selectProduct(
            @RequestParam(defaultValue = "1") Integer pageNum,   // 第几页，默认第1页
            @RequestParam(defaultValue = "16") Integer pageSize  // ✅ 每页条数，默认16条
    ) {
        IPage<ProductListVo> pageResult = iProductsService.selectProductPage(pageNum, pageSize);
        return FwResult.ok(pageResult);
    }


}

     /*   // ✅ 调用分页查询
        IPage<ProductListVo> pageResult = iProductsService.selectProductPage(pageNum, pageSize);

        // ✅ 封装返回数据
        PageVo<ProductListVo> pageVo = new PageVo<>();
        pageVo.setTotal(pageResult.getTotal());           // 总记录数（300条）
        pageVo.setPages(pageResult.getPages());           // ✅ 总页数（自动计算：300/16=19页）
        pageVo.setCurrent(pageResult.getCurrent());       // 当前页
        pageVo.setSize(pageResult.getSize());             // 每页大小
        pageVo.setRecords(pageResult.getRecords());       // 当前页数据

        return FwResult.ok(pageVo);*/
