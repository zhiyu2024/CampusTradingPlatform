package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.model.dto.aDto.ProductDto;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/product")
public class AddProductController {
    @Autowired
    IProductsService iProductsService;
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

