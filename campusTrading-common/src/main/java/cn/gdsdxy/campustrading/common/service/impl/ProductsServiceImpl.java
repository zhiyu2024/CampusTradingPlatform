package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.ProductImagesEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.ProductImagesMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.model.dto.userDto.ProductDto;
import cn.gdsdxy.campustrading.common.model.dto.userDto.ProductSearchParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.ProductUpdateParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.PageVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductDetailVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductListVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import org.apache.commons.lang3.StringUtils;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, ProductsEntity> implements IProductsService {
    @Autowired
    private  ProductsMapper productsMapper;

    @Autowired
    private ProductImagesMapper productImagesMapper;

    @Value("${file.upload-images-path}") // ✅ 从配置读取路径
    private String uploadImagesPath;
    @Override
    public IPage<ProductListVo> searchProducts(Integer pageNum, Integer pageSize, ProductSearchParam param) {
        // 1. 创建分页对象
        Page<ProductsEntity> page = new Page<>(pageNum, pageSize);

        // 2. 构建搜索条件（支持关键词、分类、砍价、折扣）
        LambdaQueryWrapper<ProductsEntity> wrapper = Wrappers.<ProductsEntity>lambdaQuery()
                // ✅ 关键词模糊搜索（商品名称）
                .like(StringUtils.isNotBlank(param.getKeyword()),
                        ProductsEntity::getProductName, param.getKeyword())

                // 分类精确搜索
                .eq(param.getCategoryId() != null,
                        ProductsEntity::getCategoryId, param.getCategoryId())

                // 是否支持砍价
                .eq(param.getIsBargainable() != null,
                        ProductsEntity::getIsBargainable, param.getIsBargainable())

                // 是否有折扣（折扣率 < 1）
                .lt(param.getHasDiscount() != null && param.getHasDiscount(),
                        ProductsEntity::getDiscountRate, BigDecimal.ONE)

                //  只查询在售商品（可选）
                .eq(ProductsEntity::getStatus, 1)

                //  按创建时间倒序（最新商品在前）
                .orderByDesc(ProductsEntity::getCreatedAt);

        // 3. 执行分页查询
        IPage<ProductsEntity> entityPage = productsMapper.selectPage(page, wrapper);

        // 4. 转换为VO并封装图片列表
        return entityPage.convert(entity -> {
            ProductListVo vo = new ProductListVo();
            BeanUtils.copyProperties(entity, vo);

            // 查询该商品的所有图片（按sort_order排序）
            List<ProductImagesEntity> images = productImagesMapper.selectList(
                    Wrappers.<ProductImagesEntity>lambdaQuery()
                            .eq(ProductImagesEntity::getProductId, entity.getProductId())
                            .orderByAsc(ProductImagesEntity::getSortOrder)
            );

            // 提取URL列表
            List<String> imageUrls = images.stream()
                    .map(ProductImagesEntity::getImageUrl)
                    .collect(Collectors.toList());

            vo.setImageUrls(imageUrls);  // 设置到VO

            return vo;
        });
    }
    @Override
    public IPage<ProductListVo> selectProductPage(Integer pageNum, Integer pageSize) {
        Page<ProductsEntity> page = new Page<>(pageNum, pageSize);
        IPage<ProductsEntity> entityPage = baseMapper.selectPage(page, null);

        return entityPage.convert(entity -> {
            ProductListVo vo = new ProductListVo();
            BeanUtils.copyProperties(entity, vo);

            // 查询该商品的所有图片，按sort_order排序
            List<ProductImagesEntity> images = productImagesMapper.selectList(
                    Wrappers.<ProductImagesEntity>lambdaQuery()
                            .eq(ProductImagesEntity::getProductId, entity.getProductId())
                            .orderByAsc(ProductImagesEntity::getSortOrder)
            );
// 转换为URL列表
            List<String> imageUrls = images.stream()
                    .map(ProductImagesEntity::getImageUrl)
                    .collect(Collectors.toList());

            vo.setImageUrls(imageUrls);
            return vo;
        });

    }

    @Override
    @Transactional
    public ProductDetailVo getProductDetail(Integer productId){
// 1. 查询商品详情（包含卖家信息）
        ProductDetailVo detailVo = productsMapper.selectProductDetailWithSeller(productId);

        if (detailVo == null) {
            throw new BusinessException(1007,"商品不存在");
        }

        // 2. 计算折后价格
        if (detailVo.getDiscountRate() != null && detailVo.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountedPrice = detailVo.getPrice()
                    .multiply(detailVo.getDiscountRate())
                    .setScale(2, RoundingMode.HALF_UP);
            detailVo.setDiscountedPrice(discountedPrice);
        } else {
            detailVo.setDiscountedPrice(detailVo.getPrice());
        }

        // 3. 查询商品图片列表
        List<String> images = productsMapper.selectProductImages(productId);
        detailVo.setImages(images);

        // 4. 更新浏览量（异步或同步）
        productsMapper.incrementViewCount(productId);

        return detailVo;
    }


    @Override
    public   FwResult deleteByProduct(Integer productId ){
        Long sellerId=SecurityUtil.getUserId();//当前用户
        ProductsEntity productsEntity=productsMapper.selectById(productId);//查询获取该商品
        if(!sellerId.equals(productsEntity.getSellerId().longValue())){//商品的卖家
            throw new BusinessException(1008,"无权限删除");
        }
        //  查询该商品所有图片
        LambdaQueryWrapper<ProductImagesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagesEntity::getProductId, productId);
        List<ProductImagesEntity> images = productImagesMapper.selectList(wrapper);

        //  删除图片表记录
        if (!images.isEmpty()) {
            productImagesMapper.delete(wrapper);
            log.info("删除商品 {} 的图片记录 {} 条", productId, images.size());
        }

        this.removeById(productId);
        log.info("删除商品 {}", productId);

//  异步删除物理文件（避免阻塞主线程）
        if (!images.isEmpty()) {
            deleteProductImagesAsync(images);
        }
        return FwResult.ok( "商品删除成功");
    }
    @Async // 需要启用 @EnableAsync
    public void deleteProductImagesAsync(List<ProductImagesEntity> images) {
        for (ProductImagesEntity image : images) {
            deletePhysicalFile(image.getImageUrl());
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVo updateProduct(ProductUpdateParam productUpdateParam) {
        // 1. 获取当前用户ID
        Long userId = SecurityUtil.getUserId();

        // 2. 验证商品是否存在且属于当前用户
        ProductsEntity product = this.getById(productUpdateParam.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!userId.equals(product.getSellerId().longValue())) {
            throw new RuntimeException("无权修改此商品");
        }

        // 3. 更新商品基本信息（只更新非空字段）
        if (productUpdateParam.getProductName() != null) {
            product.setProductName(productUpdateParam.getProductName());
        }
        if (productUpdateParam.getDescription() != null) {
            product.setDescription(productUpdateParam.getDescription());
        }
        if (productUpdateParam.getPrice() != null) {
            product.setPrice(productUpdateParam.getPrice());
        }
        if (productUpdateParam.getCategoryId() != null) {
            product.setCategoryId(productUpdateParam.getCategoryId());
        }
        if (productUpdateParam.getIsBargainable() != null) {
            product.setIsBargainable(productUpdateParam.getIsBargainable().equals(1));
        }
        if (productUpdateParam.getDiscountRate() != null) {
            product.setDiscountRate(productUpdateParam.getDiscountRate());
        }

        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        product.setUpdatedAt(currentDate);

        // 4. ✅ 执行更新
        this.updateById(product);

        // 5. ✅ 删除指定的旧图片
        if (productUpdateParam.getDeleteImageIds() != null &&
                !productUpdateParam.getDeleteImageIds().isEmpty()) {

            for (Integer imageId : productUpdateParam.getDeleteImageIds()) {
                // 查询图片信息
                ProductImagesEntity image = productImagesMapper.selectById(imageId);

                // 验证图片是否属于当前商品
                if (image != null && image.getProductId().equals(product.getProductId())) {
                    // 删除数据库记录
                    productImagesMapper.deleteById(imageId);

                    // 删除物理文件
                    deletePhysicalFile(image.getImageUrl());
                }
            }
        }

        // 6. ✅ 上传新图片
        List<String> imageUrls = new ArrayList<>();
        if (productUpdateParam.getNewImages() != null &&
                !productUpdateParam.getNewImages().isEmpty()) {

            // 查询现有图片的最大序号
            LambdaQueryWrapper<ProductImagesEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductImagesEntity::getProductId, product.getProductId());
            wrapper.orderByDesc(ProductImagesEntity::getSortOrder);
            wrapper.last("LIMIT 1");
            ProductImagesEntity lastImage = productImagesMapper.selectOne(wrapper);

            // 确定新图片的起始序号（如果商品没有图片，从0开始）
            int startIndex = (lastImage != null) ? lastImage.getSortOrder() : -1;

            // 上传新图片

            for (int i = 0; i < productUpdateParam.getNewImages().size(); i++) {
                MultipartFile file = productUpdateParam.getNewImages().get(i);
                if (!file.isEmpty()) {
                    try {
                        String savedFileName = saveFile(file, product.getProductId(), startIndex + i);
                        String relativePath = "/res/images/" + savedFileName;

                        ProductImagesEntity img = new ProductImagesEntity();
                        img.setProductId(product.getProductId());
                        img.setImageUrl(relativePath);
                        img.setSortOrder(startIndex + i + 1);
                        productImagesMapper.insert(img);

                        imageUrls.add(relativePath);
                    } catch (IOException e) {
                        log.error("图片上传失败: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
                    }
                }
            }
        }

        // 7. 查询该商品所有剩余图片（按sortOrder排序）
        LambdaQueryWrapper<ProductImagesEntity> finalWrapper = new LambdaQueryWrapper<>();
        finalWrapper.eq(ProductImagesEntity::getProductId, product.getProductId());
        finalWrapper.orderByAsc(ProductImagesEntity::getSortOrder);
        List<ProductImagesEntity> finalImages = productImagesMapper.selectList(finalWrapper);

        // 获取所有图片URL
        imageUrls = finalImages.stream()
                .map(ProductImagesEntity::getImageUrl)
                .collect(Collectors.toList());

        // 8. 构建返回 VO
        ProductVo vo = new ProductVo();
        vo.setProductId(Long.valueOf(product.getProductId()));
        vo.setSellerId(userId);
        vo.setProductName(product.getProductName());
        vo.setImageUrl(imageUrls); //
        vo.setMessage("商品更新成功");

        return vo;
    }

    /**
     * 删除物理文件（建议异步执行）
     */
    private void deletePhysicalFile(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.startsWith("/res/images/")) {
                String fileName = imageUrl.substring("/res/images/".length());
                File file = new File(uploadImagesPath + fileName);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("成功删除旧图片文件: {}", file.getAbsolutePath());
                    } else {
                        log.warn("删除文件失败: {}", file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("删除物理文件失败: {}", imageUrl, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVo AddProduct(ProductDto productDto) {
        try {
            // 1. 获取当前登录用户ID 获取销售者Id,数据库表用Int 但是后端传过去的Long 数据库可以自动兼容
            Long sellerId = SecurityUtil.getUserId(); //解析token,获取token里面的userId

            // 2. 保存商品基本信息（✅ 所有类型转换已修复）  创建新的商品信息  然后将创建的新商品实体,插入到商品表里面
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

            this.save(product);//利用service的业务层的保存到数据库的方法,插入新的商品实体到数据库里面 this就是当前的业务层实体
             //插入保存新的商品实体

            //获取token里面的用户id
            Long userId = SecurityUtil.getUserId();

            // 3. 处理文件上传  //解决完了商品表的保存,就可以处理文件上传的商品图片,将上传的图片列表转换成文件,保存在后端的图片路径
            List<String> imageUrls = new ArrayList<>(); //设置接受图片Url的路径
            //4.验证是否有商品图片
            if (productDto.getImageUrl() != null && !productDto.getImageUrl().isEmpty()) {//查看兰传过来的图片列表为不为空
                for (int i = 0; i < productDto.getImageUrl().size(); i++) {//遍历文件列表
                    MultipartFile file = productDto.getImageUrl().get(i);//获取每一个文件的路径和商品图片
                    if (!file.isEmpty()) {//验证
                        // ✅ 传递 productId 和索引 i  后面的文件上传方法需要用到
                        String savedFileName = saveFile(file, product.getProductId(), i);//上传路径本身 商品的ID 还有序号
                        String relativePath = "/res/images/" + savedFileName;//设置保存的路径

                        // 保存到图片表  图片数据库的保存填充  //重复遍历填充 进图片表里面 因为有几张图片 保存几张图片
                        ProductImagesEntity img = new ProductImagesEntity();
                        img.setProductId(product.getProductId()); // ✅ 这里直接传 Integer
                        img.setImageUrl(relativePath);
                        img.setSortOrder(i); // ✅ 按上传顺序排序
                        productImagesMapper.insert(img);//另一种保存填充数据库的方法 业务层之外的 Mapper的insert插入方法
                        imageUrls.add(relativePath);//设置接受图片Url的路径  填充进在列表里面
                        //   是传进这里:        List<String> imageUrls = new ArrayList<>(); //设置接受图片Url的路径
                    }
                }
            }

            // 4. 构建返回 VO
            ProductVo vo=new ProductVo();
            vo.setProductId(Long.valueOf(product.getProductId()));
            vo.setSellerId(userId);
            vo.setProductName(product.getProductName());
            vo.setImageUrl(imageUrls);//设置返回给前端的商品列表实体类
            vo.setMessage("商品发布成功");

            return vo;

        } catch (Exception e) {
            log.error("商品发布失败", e);
            throw new RuntimeException("商品发布失败: " + e.getMessage());
        }
    }

    /**
     *  文件上传方法（内聚在 Service 中）
     *  1. 获取上传的文件 然后 2.生成文件名 3.然后确定保存的文件名是存在的并且可以访问  4.返回创建的文件名
     */
    private String saveFile(MultipartFile file, Integer productId, int index) throws IOException {
        // 1. 获取原文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 2. 生成文件名
        String newFileName = String.format("%d_%d_%d%s",
                productId,                    // 商品ID
                System.currentTimeMillis(),   // 时间戳（毫秒）
                index,                    // 序号（从1开始）
                suffix                        // 文件后缀
        );

        // 3. 确保目录存在
        File dir = new File(uploadImagesPath); //看上传的文件的路径里的图片是否存在 全局文件配置
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 保存文件
        File destFile = new File(dir, newFileName);
        file.transferTo(destFile);

        return newFileName; // 返回新文件名  不包括路劲
    }
}