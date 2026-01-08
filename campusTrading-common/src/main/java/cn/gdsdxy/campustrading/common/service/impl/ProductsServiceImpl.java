package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.ProductImagesEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.mapper.ProductImagesMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.model.dto.aDto.ProductDto;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductVo;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, ProductsEntity> implements IProductsService {

    @Autowired
    private ProductImagesMapper productImagesMapper;

    @Value("${file.upload-images-path}") // ✅ 从配置读取路径
    private String uploadImagesPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVo AddProduct(ProductDto productDto) {
        try {
            // 1. 获取当前登录用户ID
            Long sellerId = SecurityUtil.getUserId();

            // 2. 保存商品基本信息（✅ 所有类型转换已修复）
            ProductsEntity product = new ProductsEntity();
            product.setProductName(productDto.getProductName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setCategoryId(productDto.getCategoryId());
            product.setSellerId(sellerId.intValue());

            product.setStock((byte) 1); // ✅ 强制转换为 Byte
            product.setStatus((byte) 1); // ✅ 强制转换为 Byte（1-在售）
            product.setViewCount(0); // ✅ int 自动装箱为 Integer
            Integer isBargainable = productDto.getIsBargainable();// ✅ isBargainable 处理：前端传 Integer，实体是 Boolean
            product.setIsBargainable(isBargainable != null && isBargainable == 1); // 1=true, 0=false
            product.setDiscountRate(productDto.getDiscountRate());

            // 获取当前时间（版本兼容处理）
            LocalDateTime now = LocalDateTime.now();
            Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            product.setCreatedAt(currentDate);
            product.setUpdatedAt(currentDate);

            this.save(product);

            // ✅ getProductId() 返回 Integer，需要转换
            Long userId = SecurityUtil.getUserId();

            // 3. 处理文件上传（✅ 文件名格式已修改）
            List<String> imageUrls = new ArrayList<>();

            if (productDto.getImageUrl() != null && !productDto.getImageUrl().isEmpty()) {
                for (int i = 0; i < productDto.getImageUrl().size(); i++) {
                    MultipartFile file = productDto.getImageUrl().get(i);
                    if (!file.isEmpty()) {
                        // ✅ 传递 productId 和索引 i
                        String savedFileName = saveFile(file, product.getProductId(), i);
                        String relativePath = "/res/images/" + savedFileName;

                        // 保存到图片表
                        ProductImagesEntity img = new ProductImagesEntity();
                        img.setProductId(product.getProductId()); // ✅ 这里直接传 Integer
                        img.setImageUrl(relativePath);
                        img.setSortOrder(i); // ✅ 按上传顺序排序
                        productImagesMapper.insert(img);

                        imageUrls.add(relativePath);
                    }
                }
            }

            // 4. 构建返回 VO
            ProductVo vo = new ProductVo();
            vo.setProductId(Long.valueOf(product.getProductId()));
            vo.setSellerId(userId);
            vo.setProductName(product.getProductName());
            vo.setImageUrl(imageUrls);
            vo.setMessage("商品发布成功");

            return vo;

        } catch (Exception e) {
            log.error("商品发布失败", e);
            throw new RuntimeException("商品发布失败: " + e.getMessage());
        }
    }

    /**
     * ✅ 文件上传方法（内聚在 Service 中）
     * @param file 文件
     * @param productId 商品ID
     * @param index 图片序号（从0开始）
     * @return 新文件名
     */
    private String saveFile(MultipartFile file, Integer productId, int index) throws IOException {
        // 1. 获取原文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 2. ✅ 新命名规范：productId_时间戳_序号.后缀
        // 例如：101_1704698888000_1.jpg
        String newFileName = String.format("%d_%d_%d%s",
                productId,                    // 商品ID
                System.currentTimeMillis(),   // 时间戳（毫秒）
                index,                    // 序号（从1开始）
                suffix                        // 文件后缀
        );

        // 3. 确保目录存在
        File dir = new File(uploadImagesPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 保存文件
        File destFile = new File(dir, newFileName);
        file.transferTo(destFile);

        return newFileName; // 返回新文件名
    }
}