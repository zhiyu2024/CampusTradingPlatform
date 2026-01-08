package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.ProductImagesEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.mapper.ProductImagesMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.model.dto.aDto.ProductDto;
import cn.gdsdxy.campustrading.common.model.vo.userVo.ProductVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    private  ProductsMapper productsMapper;

    @Autowired
    private ProductImagesMapper productImagesMapper;

    @Value("${file.upload-images-path}") // ✅ 从配置读取路径
    private String uploadImagesPath;

    @Override
    public IPage<ProductsEntity> selectProductPage(Integer pageNum, Integer pageSize) {
        // 创建分页对象（当前页，每页大小）
        Page<ProductsEntity> page = new Page<>(pageNum, pageSize);

        // 执行分页查询，返回结果自动包含总页数
        return productsMapper.selectPage(page, null);
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
            ProductVo vo = new ProductVo();
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