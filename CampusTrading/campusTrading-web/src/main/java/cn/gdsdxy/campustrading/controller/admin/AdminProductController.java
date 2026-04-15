package cn.gdsdxy.campustrading.controller.admin;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.ProductImageVo;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.ProductListVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductDetailVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.DoubaoService;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "管理员商品管理", description = "管理员商品相关接口")
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private IProductsService productsService;

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private DoubaoService doubaoService;

    @GetMapping
    public FwResult<IPage<ProductListVo>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String product_name,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) Integer category_id,
            @RequestParam(required = false) Byte audit_status) {
        log.info("管理员查询商品列表, page={}, pageSize={}, product_name={}, status={}, category_id={}, audit_status={}",
                page, pageSize, product_name, status, category_id, audit_status);
        Page<ProductListVo> pageParam = new Page<>(page, pageSize);
        IPage<ProductListVo> result = productsMapper.selectProductListWithDetails(pageParam, product_name, status, category_id, audit_status);
        
        // 为每个商品加载图片
        if (result.getRecords() != null) {
            for (ProductListVo product : result.getRecords()) {
                List<ProductImageVo> images = productsMapper.selectProductImagesForList(product.getProductId());
                product.setImages(images);
            }
        }
        
        return FwResult.ok(result);
    }

    @GetMapping("/{id}")
    public FwResult<ProductDetailVo> getDetail(@PathVariable Integer id) {
        log.info("管理员查询商品详情, id={}", id);
        ProductDetailVo product = productsMapper.selectProductDetailWithSeller(id);
        if (product == null) {
            return FwResult.fail("商品不存在");
        }
        
        ProductsEntity productEntity = productsService.getById(id);
        if (productEntity != null) {
            product.setAuditStatus(productEntity.getAuditStatus());
            product.setAuditResult(productEntity.getAuditResult());
        }
        
        List<String> images = productsMapper.selectProductImages(id);
        product.setImages(images);
        
        return FwResult.ok(product);
    }

    @PostMapping
    public FwResult<String> create(@RequestBody ProductsEntity product) {
        log.info("管理员创建商品, productName={}", product.getProductName());
        productsService.save(product);
        return FwResult.ok("创建成功");
    }

    @PutMapping("/{id}")
    public FwResult<String> update(@PathVariable Integer id, @RequestBody ProductsEntity product) {
        log.info("管理员更新商品, id={}", id);
        product.setProductId(id);
        productsService.updateById(product);
        return FwResult.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public FwResult<String> delete(@PathVariable Integer id) {
        log.info("管理员删除商品, id={}", id);
        productsService.removeById(id);
        return FwResult.ok("删除成功");
    }

    @PutMapping("/{id}/status")
    public FwResult<String> toggleStatus(@PathVariable Integer id, @RequestBody ProductsEntity param) {
        log.info("管理员修改商品状态, id={}, status={}", id, param.getStatus());
        ProductsEntity product = new ProductsEntity();
        product.setProductId(id);
        product.setStatus(param.getStatus());
        productsService.updateById(product);
        return FwResult.ok("状态更新成功");
    }

    @PutMapping("/{id}/audit")
    public FwResult<String> auditProduct(@PathVariable Integer id, @RequestBody ProductsEntity param) {
        log.info("管理员审核商品, id={}, auditStatus={}, auditResult={}", id, param.getAuditStatus(), param.getAuditResult());
        ProductsEntity product = new ProductsEntity();
        product.setProductId(id);
        product.setAuditStatus(param.getAuditStatus());
        product.setAuditResult(param.getAuditResult());
        productsService.updateById(product);
        return FwResult.ok("审核成功");
    }

    @PostMapping("/{id}/ai-audit")
    public FwResult<ProductDetailVo> aiAuditProduct(@PathVariable Integer id) {
        log.info("AI智能审核商品, id={}", id);
        ProductsEntity product = productsService.getById(id);
        if (product == null) {
            return FwResult.fail("商品不存在");
        }

        DoubaoService.AuditResult auditResult = doubaoService.auditProductContent(
                product.getProductName(),
                product.getDescription(),
                null
        );

        // 检查AI审核是否成功
        if (auditResult.getStatus() == 0 && "审核失败".equals(auditResult.getReason())) {
            return FwResult.fail("AI审核服务异常，请稍后重试");
        }

        product.setAuditStatus(auditResult.getStatus());
        product.setAuditResult(auditResult.getReason());
        productsService.updateById(product);

        ProductDetailVo productDetail = productsMapper.selectProductDetailWithSeller(id);
        if (productDetail != null) {
            productDetail.setAuditStatus(auditResult.getStatus());
            productDetail.setAuditResult(auditResult.getReason());
            
            List<String> images = productsMapper.selectProductImages(id);
            productDetail.setImages(images);
        }

        return FwResult.ok(productDetail);
    }
}
