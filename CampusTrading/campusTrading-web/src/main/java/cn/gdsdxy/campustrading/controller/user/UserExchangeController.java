package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.DoubaoService;
import cn.gdsdxy.campustrading.common.service.IProductsService;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import cn.gdsdxy.campustrading.common.service.MatchResult;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/user/exchange")
@Tag(name = "用户以物换物", description = "以物换物相关接口")
public class UserExchangeController {

    @Autowired
    IProductsService iProductsService;

    @Autowired
    IUsersService iUserService;

    @Autowired
    DoubaoService doubaoService;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExchangeRequest {
        private String myItem;
        private String myWant;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExchangeMatchItem {
        private Integer productId;
        private String productName;
        private String productImage;
        private String sellerName;
        private Integer sellerId;
        private String otherWant;
        private Integer matchScore;
        private String matchReason;
        private Boolean recommend;
    }

    @GetMapping("/my")
    public FwResult<ProductsEntity> getMyExchange() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return FwResult.fail("请先登录");
        }

        LambdaQueryWrapper<ProductsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductsEntity::getSellerId, userId)
                .eq(ProductsEntity::getExchangeType, 1)
                .orderByDesc(ProductsEntity::getCreatedAt)
                .last("LIMIT 1");

        ProductsEntity exchange = iProductsService.getOne(wrapper);
        return FwResult.ok(exchange);
    }

    @GetMapping("/my/list")
    public FwResult<List<ProductsEntity>> getMyExchangeList() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return FwResult.fail("请先登录");
        }

        LambdaQueryWrapper<ProductsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductsEntity::getSellerId, userId)
                .eq(ProductsEntity::getExchangeType, 1)
                .orderByDesc(ProductsEntity::getCreatedAt);

        List<ProductsEntity> list = iProductsService.list(wrapper);
        return FwResult.ok(list);
    }

    @PostMapping("/publish")
    public FwResult<ProductsEntity> publishExchange(@RequestBody ExchangeRequest request) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return FwResult.fail("请先登录");
        }

        if (request.getMyItem() == null || request.getMyItem().trim().isEmpty()) {
            return FwResult.fail("请填写你要交换的物品");
        }

        LambdaQueryWrapper<ProductsEntity> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ProductsEntity::getSellerId, userId)
                .eq(ProductsEntity::getExchangeType, 1)
                .eq(ProductsEntity::getProductName, request.getMyItem())
                .eq(ProductsEntity::getExchangeWant, request.getMyWant() != null ? request.getMyWant() : "");
        ProductsEntity existing = iProductsService.getOne(checkWrapper);

        if (existing != null) {
            return FwResult.fail("该换物需求已发布过，请勿重复申请");
        }

        ProductsEntity exchange = new ProductsEntity();
        exchange.setProductName(request.getMyItem());
        exchange.setExchangeWant(request.getMyWant());
        exchange.setExchangeType((byte) 1);
        exchange.setSellerId(userId);
        exchange.setStatus((byte) 0);
        exchange.setCategoryId(1);
        exchange.setPrice(new BigDecimal("0"));
        exchange.setStock((byte)1);
        exchange.setIsBargainable(true);
        exchange.setDiscountRate(new BigDecimal("1"));
        iProductsService.save(exchange);

        return FwResult.ok(exchange);
    }

    @PostMapping("/match")
    public FwResult<List<ExchangeMatchItem>> doMatch() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return FwResult.fail("请先登录");
        }

        LambdaQueryWrapper<ProductsEntity> myWrapper = new LambdaQueryWrapper<>();
        myWrapper.eq(ProductsEntity::getSellerId, userId)
                .eq(ProductsEntity::getExchangeType, 1)
                .orderByDesc(ProductsEntity::getCreatedAt)
                .last("LIMIT 1");
        ProductsEntity myExchange = iProductsService.getOne(myWrapper);

        if (myExchange == null) {
            return FwResult.fail("请先发布你的换物需求");
        }

        String myItem = myExchange.getProductName();
        String myWant = myExchange.getExchangeWant();

        LambdaQueryWrapper<ProductsEntity> otherWrapper = new LambdaQueryWrapper<>();
        otherWrapper.eq(ProductsEntity::getExchangeType, 1)
                .ne(ProductsEntity::getSellerId, userId);

        List<ProductsEntity> others = iProductsService.list(otherWrapper);

        List<ExchangeMatchItem> results = new ArrayList<>();
        for (ProductsEntity other : others) {
            MatchResult match = doubaoService.calculateMatchScore(
                    myItem, myWant,
                    other.getProductName(), other.getExchangeWant()
            );

            UsersEntity seller = iUserService.getById(other.getSellerId());
            ExchangeMatchItem item = new ExchangeMatchItem();
            item.setProductId(other.getProductId());
            item.setProductName(other.getProductName());
            item.setProductImage("");
            item.setSellerName(seller != null ? seller.getUsername() : "匿名用户");
            item.setSellerId(other.getSellerId());
            item.setOtherWant(other.getExchangeWant());
            item.setMatchScore(match.getScore());
            item.setMatchReason(match.getReason());
            item.setRecommend(match.isRecommend());

            results.add(item);
        }

        results = results.stream()
                .sorted(Comparator.comparingInt(ExchangeMatchItem::getMatchScore).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return FwResult.ok(results);
    }

    private Integer getCurrentUserId() {
        try {
            Object userIdObj =  SecurityUtil.getUserId();
            if (userIdObj != null) {
                return Integer.parseInt(userIdObj.toString());
            }
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
        }
        return null;
    }
}
