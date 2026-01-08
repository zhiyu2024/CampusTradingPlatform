package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.model.dto.uDto.ProductDto;
import cn.gdsdxy.campustrading.common.model.dto.uDto.ProductUpdateParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j // ✅ 添加日志
@RestController
@RequestMapping("/api/user/product")
@Tag(name = "用户商品管理", description = "用户商品相关接口") // ✅ 使用 @Tag
public class UserProductController {

    @Autowired
    IProductsService iProductsService;
    @PostMapping(value = "/delete")
    public FwResult deleteByProduct(@RequestParam Integer productId){
        return iProductsService.deleteProduct(productId);
    }

    @PostMapping(value = "/update", consumes = "multipart/form-data")
    public FwResult<ProductVo> UpdateProduct(ProductUpdateParam productUpdateParam){
        ProductVo productVo=iProductsService.updateProduct(productUpdateParam);
        return FwResult.ok(productVo);
   }

    /**
     * 发布商品 (带图片)
     * 请求方式: POST (Multipart/form-data)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)//等于:multipart/form-data
    public FwResult<ProductVo> addProductCon(@ModelAttribute ProductDto productDto) {
        // @ModelAttribute 用于接收 form-data 格式的数据（包含文件）
        // 相比 @RequestBody，它更适合文件上传场景

        ProductVo productVo = iProductsService.AddProduct(productDto);
        return FwResult.ok(productVo);
    }

}
//    @PostMapping("/product")  //含图片上传,不能用 /product 要formdata  而且不能用RequestBody 要用:ModelAttribute
//    public FwResult<ProductVo> AddProductCon(@RequestBody ProductDto productDto){
//        ProductVo productVo= iProductsService.AddProduct(productDto);
//        return FwResult.ok(productVo);
//    }
