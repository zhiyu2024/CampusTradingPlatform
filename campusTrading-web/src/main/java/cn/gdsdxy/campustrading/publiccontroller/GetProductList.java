package cn.gdsdxy.campustrading.publiccontroller;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.model.vo.userVo.PageVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/public")
public class GetProductList {
    @Autowired
    private IProductsService iProductsService;

    @GetMapping("/productList")
    public FwResult selectProduct(
            @RequestParam(defaultValue = "1") Integer pageNum,   // 第几页，默认第1页
            @RequestParam(defaultValue = "16") Integer pageSize  // ✅ 每页条数，默认16条
    ) {
        // ✅ 调用分页查询
        IPage<ProductsEntity> pageResult = iProductsService.selectProductPage(pageNum, pageSize);

        // ✅ 封装返回数据
        PageVo<ProductsEntity> pageVo = new PageVo<>();
        pageVo.setTotal(pageResult.getTotal());           // 总记录数（300条）
        pageVo.setPages(pageResult.getPages());           // ✅ 总页数（自动计算：300/16=19页）
        pageVo.setCurrent(pageResult.getCurrent());       // 当前页
        pageVo.setSize(pageResult.getSize());             // 每页大小
        pageVo.setRecords(pageResult.getRecords());       // 当前页数据

        return FwResult.ok(pageVo);
    }
}
