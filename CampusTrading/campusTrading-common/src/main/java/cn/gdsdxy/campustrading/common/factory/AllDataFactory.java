//package cn.gdsdxy.campustrading.common.factory;
//
//import cn.gdsdxy.campustrading.common.entity.*;
//import cn.gdsdxy.campustrading.common.mapper.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.github.javafaker.Faker;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//public class AllDataFactory {
//
//    private static final Logger log = LoggerFactory.getLogger(AllDataFactory.class);
//    private final Faker faker = new Faker();
//    private final Random random = new Random();
//
//    // ==================== 可配置常量 ====================
//    private static final int BATCH_SIZE = 100;
//    private static final int USER_COUNT = 100;
//    private static final int PRODUCT_COUNT = 100;
//    private static final int CART_COUNT = 30;
//    private static final int MESSAGE_COUNT = 50;
//    private static final int ORDER_COUNT = 25;
//    private static final int PRODUCT_IMAGES_PER_PRODUCT = 3; // 每张商品图片数量
//
//    @Autowired private UsersMapper usersMapper;
//    @Autowired private ProductsMapper productsMapper;
//    @Autowired private CategoriesMapper categoriesMapper;
//    @Autowired private CartMapper cartMapper;
//    @Autowired private MessagesMapper messagesMapper;
//    @Autowired private OrdersMapper ordersMapper;
//    @Autowired private ProductImagesMapper productImagesMapper;
//
//    private final Map<String, List<String>> imageCategoryMap = new HashMap<>();
//
//    public AllDataFactory() {
//        initializeImageMap();
//    }
//
//    /**
//     * 主入口：生成所有测试数据（优化版）
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void generateAllData() {
//        log.info("\n" + "=".repeat(60));
//        log.info("🚀 开始生成全表真实数据 - 增强版");
//        log.info("=".repeat(60));
//        long startTime = System.currentTimeMillis();
//
//        GenerationStats stats = new GenerationStats();
//
//        try {
//            // 1. 清空并重置表
//            log.info("\n📦 阶段1：清空并重置数据表...");
//            truncateAllTablesWithReset();
//            stats.clearTime = System.currentTimeMillis() - startTime;
//
//            // 2. 生成用户
//            log.info("\n👥 阶段2：生成用户数据...");
//            long userStart = System.currentTimeMillis();
//            List<UsersEntity> users = generateUsers();
//            usersMapper.batchInsert(users);
//            List<UsersEntity> dbUsers = usersMapper.selectList(null);
//            stats.userCount = dbUsers.size();
//            stats.userTime = System.currentTimeMillis() - userStart;
//            log.info("✅ 用户生成完成：{} 条 (耗时：{} ms)", stats.userCount, stats.userTime);
//
//            // 3. 生成商品
//            log.info("\n🛍️ 阶段3：生成商品数据...");
//            long productStart = System.currentTimeMillis();
//            List<ProductsEntity> products = generateProducts(dbUsers);
//            productsMapper.batchInsert(products);
//            List<ProductsEntity> productsWithId = productsMapper.selectList(null);
//            stats.productCount = productsWithId.size();
//            stats.productTime = System.currentTimeMillis() - productStart;
//            log.info("✅ 商品生成完成：{} 条 (耗时：{} ms)", stats.productCount, stats.productTime);
//
//            // 4. 生成商品图片
//            log.info("\n🖼️ 阶段4：生成商品图片...");
//            long imageStart = System.currentTimeMillis();
//            stats.imageCount = generateProductImagesWithStats(productsWithId);
//            stats.imageTime = System.currentTimeMillis() - imageStart;
//            log.info("✅ 商品图片生成完成：{} 张 (耗时：{} ms)", stats.imageCount, stats.imageTime);
//
//            // 5. 生成购物车
//            log.info("\n🛒 阶段5：生成购物车数据...");
//            long cartStart = System.currentTimeMillis();
//            List<CartEntity> carts = generateCarts(dbUsers, productsWithId);
//            cartMapper.batchInsert(carts);
//            stats.cartCount = carts.size();
//            stats.cartTime = System.currentTimeMillis() - cartStart;
//            log.info("✅ 购物车生成完成：{} 条 (耗时：{} ms)", stats.cartCount, stats.cartTime);
//
//            // 6. 生成消息
//            log.info("\n💬 阶段6：生成消息数据...");
//            long msgStart = System.currentTimeMillis();
//            List<MessagesEntity> messages = generateMessages(dbUsers, productsWithId);
//            messagesMapper.batchInsert(messages);
//            stats.messageCount = messages.size();
//            stats.messageTime = System.currentTimeMillis() - msgStart;
//            log.info("✅ 消息生成完成：{} 条 (耗时：{} ms)", stats.messageCount, stats.messageTime);
//
//            // 7. 生成订单
//            log.info("\n📋 阶段7：生成订单数据...");
//            long orderStart = System.currentTimeMillis();
//            List<OrdersEntity> orders = generateOrders(dbUsers, productsWithId);
//            ordersMapper.batchInsert(orders);
//            stats.orderCount = orders.size();
//            stats.orderTime = System.currentTimeMillis() - orderStart;
//            log.info("✅ 订单生成完成：{} 条 (耗时：{} ms)", stats.orderCount, stats.orderTime);
//
//            // 8. 打印统计报告
//            stats.totalTime = System.currentTimeMillis() - startTime;
//            log.info("\n" + "=".repeat(60));
//            log.info("📊 数据生成统计报告");
//            log.info("=".repeat(60));
//            log.info("{}", stats);
//            log.info("=".repeat(60));
//            log.info("🎉 全表数据生成成功！");
//            log.info("=".repeat(60));
//
//        } catch (Exception e) {
//            log.error("❌ 数据生成失败", e);
//            throw new RuntimeException("数据生成失败: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 清空并重置自增ID（解决ID从200开始的问题）
//     */
//    private void truncateAllTablesWithReset() {
//        try {
//            // 注意：实际项目中请谨慎使用 TRUNCATE，它会重置自增ID
//            log.warn("⚠️  正在执行 TRUNCATE 操作，将重置所有自增ID！");
//
//            // 由于 MyBatis-Plus 的 delete(null) 不会重置自增ID
//            // 这里使用原生 SQL 执行 TRUNCATE
//            // 请确保你的 Mapper 有 @Update 注解的方法执行 TRUNCATE
//            // 或者手动在数据库执行以下 SQL：
//
//            // TRUNCATE TABLE product_images;
//            // TRUNCATE TABLE cart;
//            // TRUNCATE TABLE messages;
//            // TRUNCATE TABLE orders;
//            // TRUNCATE TABLE products;
//            // TRUNCATE TABLE users;
//
//            // 临时方案：先删除，再重置（需要数据库权限）
//            productImagesMapper.delete(null);
//            cartMapper.delete(null);
//            messagesMapper.delete(null);
//            ordersMapper.delete(null);
//            productsMapper.delete(null);
//            usersMapper.delete(null);
//
//            // 如果使用的是 MySQL，可以在数据库手动执行：
//            // ALTER TABLE users AUTO_INCREMENT = 1;
//            // ALTER TABLE products AUTO_INCREMENT = 1;
//            // ALTER TABLE orders AUTO_INCREMENT = 1;
//
//            log.info("✅ 数据表已清空（自增ID未重置，如需重置请手动执行ALTER TABLE）");
//        } catch (Exception e) {
//            log.error("清空数据表失败", e);
//            throw new RuntimeException("清空数据表失败: " + e.getMessage(), e);
//        }
//    }
//
//    // ==================== 数据生成方法 ====================
//
//    private List<UsersEntity> generateUsers() {
//        List<UsersEntity> list = new ArrayList<>(USER_COUNT);
//        for (int i = 0; i < USER_COUNT; i++) {
//            UsersEntity user = new UsersEntity();
//            user.setStudentNo(String.format("2024%05d", i + 1));
//            user.setUsername("user" + (i + 1));
//            user.setPassword("$2a$10$EncryptedPasswordExample");
//            user.setNickname(faker.name().firstName() + (random.nextInt(99) + 1) + "号");
//            user.setPhone(faker.phoneNumber().cellPhone());
//            user.setCampus(randomWeightedCampus());
//            user.setRole((byte) (i == 0 ? 1 : 0));
//            user.setStatus((byte) 1);
//            user.setCreatedAt(new Date());
//            list.add(user);
//        }
//        return list;
//    }
//
//    private List<ProductsEntity> generateProducts(List<UsersEntity> users) {
//        List<CategoriesEntity> categories = categoriesMapper.selectList(null);
//        if (categories == null || categories.isEmpty()) {
//            throw new RuntimeException("商品分类表为空，请先初始化分类数据");
//        }
//        validateCategorySize(categories);
//
//        List<ProductsEntity> list = new ArrayList<>(PRODUCT_COUNT);
//        for (int i = 0; i < PRODUCT_COUNT; i++) {
//            ProductsEntity product = new ProductsEntity();
//            CategoriesEntity category = randomCategoryWithWeight(categories);
//
//            product.setCategoryId(category.getCategoryId());
//            product.setProductName(generateTitleByCategory(category.getCategoryName()));
//            product.setDescription(generateDescriptionByCategory(category.getCategoryName()));
//            product.setPrice(generatePriceByCategory(category.getCategoryName()));
//
//            Integer sellerId = randomUserIdExcludeAdmin(users);
//            product.setSellerId(sellerId);
//            product.setStock((byte) (random.nextInt(5) + 1));
//            product.setStatus((byte) (random.nextDouble() > 0.3 ? 1 : 2));
//            product.setViewCount(random.nextInt(500) + 10);
//            product.setIsBargainable(random.nextBoolean());
//            product.setDiscountRate(generateDiscountRate());
//            product.setCreatedAt(randomDateInLast30Days());
//            product.setUpdatedAt(new Date());
//            list.add(product);
//        }
//        return list;
//    }
//
//    /**
//     * 生成商品图片并返回生成数量
//     */
//    private int generateProductImagesWithStats(List<ProductsEntity> products) {
//        if (products == null || products.isEmpty()) {
//            log.warn("商品列表为空，跳过图片生成");
//            return 0;
//        }
//
//        List<ProductImagesEntity> list = new ArrayList<>();
//        for (ProductsEntity product : products) {
//            if (product.getProductId() == null) {
//                log.error("⚠️  商品ID为null，跳过：{}", product.getProductName());
//                continue;
//            }
//
//            List<String> categoryImages = imageCategoryMap.get(String.valueOf(product.getCategoryId()));
//            if (categoryImages == null || categoryImages.isEmpty()) {
//                log.warn("分类ID {} 未找到图片映射", product.getCategoryId());
//                continue;
//            }
//
//            int imageCount = PRODUCT_IMAGES_PER_PRODUCT;
//            Set<Integer> usedIndices = new HashSet<>();
//
//            for (int j = 0; j < imageCount && j < categoryImages.size(); j++) {
//                ProductImagesEntity image = new ProductImagesEntity();
//                image.setProductId(product.getProductId());
//
//                // 确保不重复选择同一张图片
//                int imgIndex;
//                do {
//                    imgIndex = random.nextInt(categoryImages.size());
//                } while (usedIndices.contains(imgIndex) && usedIndices.size() < categoryImages.size());
//                usedIndices.add(imgIndex);
//
//                image.setImageUrl("http://localhost:8090/images/" + categoryImages.get(imgIndex));
//                image.setSortOrder(j);
//                list.add(image);
//            }
//        }
//
//        if (!list.isEmpty()) {
//            // 分批插入
//            for (int i = 0; i < list.size(); i += BATCH_SIZE) {
//                List<ProductImagesEntity> batch = list.subList(i, Math.min(i + BATCH_SIZE, list.size()));
//                productImagesMapper.batchInsert(batch);
//            }
//            log.info("成功插入 {} 张图片（分 {} 批）", list.size(), (list.size() + BATCH_SIZE - 1) / BATCH_SIZE);
//        }
//        return list.size();
//    }
//
//    private List<CartEntity> generateCarts(List<UsersEntity> users, List<ProductsEntity> products) {
//        Set<String> uniqueKey = new HashSet<>();
//        List<CartEntity> list = new ArrayList<>(CART_COUNT);
//
//        for (int i = 0; i < CART_COUNT; i++) {
//            Integer userId = randomUserId(users);
//            Integer productId = randomProductId(products);
//            String key = userId + "-" + productId;
//
//            if (uniqueKey.contains(key)) {
//                continue; // 避免重复
//            }
//            uniqueKey.add(key);
//
//            CartEntity cart = new CartEntity();
//            cart.setUserId(userId);
//            cart.setProductId(productId);
//            cart.setCreatedAt(randomDateInLast30Days());
//            list.add(cart);
//        }
//        return list;
//    }
//
//    private List<MessagesEntity> generateMessages(List<UsersEntity> users, List<ProductsEntity> products) {
//        List<MessagesEntity> list = new ArrayList<>(MESSAGE_COUNT);
//        for (int i = 0; i < MESSAGE_COUNT; i++) {
//            ProductsEntity product = randomProduct(products);
//            MessagesEntity msg = new MessagesEntity();
//            msg.setProductId(product.getProductId());
//            msg.setSenderId(randomUserId(users));
//            msg.setReceiverId(product.getSellerId());
//            msg.setContent(generateMessageContent());
//            msg.setMessageType((byte) (random.nextInt(3) + 1));
//            msg.setIsRead((byte) (random.nextBoolean() ? 1 : 0));
//            msg.setCreatedAt(randomDateInLast30Days());
//            list.add(msg);
//        }
//        return list;
//    }
//
//    private List<OrdersEntity> generateOrders(List<UsersEntity> users, List<ProductsEntity> products) {
//        List<ProductsEntity> soldProducts = products.stream()
//                .filter(p -> p.getStatus() != null && p.getStatus().intValue() == 2)
//                .collect(Collectors.toList());
//
//        if (soldProducts.isEmpty()) {
//            log.warn("❌ 没有已售出的商品，无法生成订单");
//            return Collections.emptyList();
//        }
//
//        int actualOrderCount = Math.min(ORDER_COUNT, soldProducts.size());
//        List<OrdersEntity> list = new ArrayList<>(actualOrderCount);
//
//        for (int i = 0; i < actualOrderCount; i++) {
//            ProductsEntity product = soldProducts.get(i);
//            UsersEntity buyer = randomUser(users);
//
//            // 确保买家≠卖家
//            int attempts = 0;
//            while (buyer.getUserId().equals(product.getSellerId()) && attempts < 10) {
//                buyer = randomUser(users);
//                attempts++;
//            }
//
//            OrdersEntity order = new OrdersEntity();
//            order.setOrderNo(generateOrderNo());
//            order.setProductId(product.getProductId());
//            order.setBuyerId(buyer.getUserId());
//            order.setSellerId(product.getSellerId());
//            order.setStatus((byte) (random.nextBoolean() ? 1 : 0));
//            order.setTotalAmount(product.getPrice());
//            order.setBuyerNote(buyer.getUserId() % 3 == 0 ? "麻烦尽快发货，急用" : "");
//            order.setCreatedAt(randomDateInLast30Days());
//            order.setUpdatedAt(new Date());
//            list.add(order);
//        }
//        return list;
//    }
//
//    // ==================== 辅助工具方法 ====================
//
//    private void initializeImageMap() {
//        imageCategoryMap.put("1", Arrays.asList("book1.png", "book2.png", "book3.png", "book4.png", "book5.png", "book6.png", "book7.png", "book8.png", "book9.png", "book10.png"));
//        imageCategoryMap.put("2", Arrays.asList("ear1.png", "ear2.png", "ear3.png", "ear4.png", "ear5.png", "phone1.png", "phone2.png", "phone3.png", "phone4.png", "phone5.png", "camera1.png", "camera2.png", "camera3.png", "camera4.png", "camera5.png", "switch1.png", "switch2.png", "switch3.png", "switch4.png", "switch5.png", "computed1.png", "computed2.png", "computed3.png", "computed4.png", "computed5.png"));
//        imageCategoryMap.put("3", Arrays.asList("live1.png", "live2.png", "live3.png", "live4.png", "live5.png", "live6.png", "live7.png", "live8.png", "live9.png", "live10.png"));
//        imageCategoryMap.put("4", Arrays.asList("bike1.png", "bike2.png", "bike3.png", "bike4.png", "bike5.png", "music1.png", "music2.png", "music3.png", "music4.png", "music5.png", "music6.png"));
//        imageCategoryMap.put("5", Arrays.asList("material1.png", "material2.png", "material3.png", "material4.png", "material5.png", "material6.png", "material7.png"));
//        imageCategoryMap.put("6", Arrays.asList("phone1.png", "phone2.png", "phone3.png", "phone4.png", "phone5.png", "tablet1.png", "tablet2.png", "tablet3.png", "tablet4.png"));
//    }
//
//    private String randomWeightedCampus() {
//        double r = random.nextDouble();
//        if (r < 0.60) return "东区";
//        if (r < 0.85) return "西区";
//        return "南区";
//    }
//
//    private CategoriesEntity randomCategoryWithWeight(List<CategoriesEntity> categories) {
//        if (categories == null || categories.size() < 5) {
//            throw new IllegalArgumentException("分类列表至少需要5个元素");
//        }
//        double r = random.nextDouble();
//        if (r < 0.30) return categories.get(0);
//        if (r < 0.55) return categories.get(1);
//        if (r < 0.75) return categories.get(2);
//        if (r < 0.90) return categories.get(3);
//        return categories.get(4);
//    }
//
//    private void validateCategorySize(List<CategoriesEntity> categories) {
//        if (categories.size() < 5) {
//            log.warn("⚠️  分类数量不足5个，当前只有 {} 个，权重分配可能异常", categories.size());
//        }
//    }
//
//    private String generateTitleByCategory(String categoryName) {
//        switch (categoryName) {
//            case "教材资料":
//                return String.format("%s %s %d成新", faker.book().title(), faker.book().author(), 70 + random.nextInt(30));
//            case "电子产品":
//                return String.format("%s %s 功能完好", faker.commerce().productName(), faker.options().option("耳机", "充电宝", "键盘", "鼠标"));
//            case "生活用品":
//                return String.format("闲置%s %s", faker.options().option("台灯", "风扇", "收纳盒", "衣架"), random.nextBoolean() ? "几乎全新" : "9成新");
//            case "运动乐器":
//                return String.format("%s %s 适合初学者", faker.options().option("吉他", "尤克里里", "篮球", "羽毛球拍"), faker.options().option("入门级", "进阶级"));
//            case "考研资料":
//                return String.format("%s考研 %s 真题+笔记", faker.options().option("计算机", "数学", "英语", "政治"), faker.options().option("历年真题", "复习全书", "冲刺卷"));
//            case "手机平板":
//                return String.format("%s %s %d成新 无拆修", faker.options().option("iPhone", "小米", "华为", "iPad"), faker.commerce().productName(), 80 + random.nextInt(20));
//            default:
//                return String.format("优质%s 低价转让", categoryName);
//        }
//    }
//
//    private String generateDescriptionByCategory(String categoryName) {
//        String baseDesc = "【%s】%s，%s。%s 可小刀，%s。";
//        switch (categoryName) {
//            case "教材资料":
//                return String.format(baseDesc, categoryName, "正版教材，划重点清晰", "有少量笔记但不影响阅读", "毕业清仓", random.nextBoolean() ? "包邮" : "自提");
//            case "电子产品":
//                return String.format(baseDesc, categoryName, "功能一切正常，无损坏", "配件齐全", "寝室闲置", "支持验机");
//            case "生活用品":
//                return String.format(baseDesc, categoryName, "购买后使用次数少", "保存完好", "搬家急出", "可小刀");
//            case "运动乐器":
//                return String.format(baseDesc, categoryName, "保养得当", random.nextBoolean() ? "有入门教程" : "附赠配件", "兴趣转移", "价格面议");
//            case "考研资料":
//                return String.format(baseDesc, categoryName, "上岸学长自用", "包含重点标注和心得", "成功上岸", "赠送电子版");
//            case "手机平板":
//                return String.format(baseDesc, categoryName, "外观轻微使用痕迹", "功能完好，电池健康", "换新出售", "非诚勿扰");
//            default:
//                return String.format("【%s】物品状况良好，有意者请联系", categoryName);
//        }
//    }
//
//    private BigDecimal generatePriceByCategory(String categoryName) {
//        double basePrice;
//        switch (categoryName) {
//            case "教材资料": basePrice = 20 + random.nextDouble() * 80; break;
//            case "电子产品": basePrice = 50 + random.nextDouble() * 300; break;
//            case "生活用品": basePrice = 10 + random.nextDouble() * 50; break;
//            case "运动乐器": basePrice = 30 + random.nextDouble() * 200; break;
//            case "考研资料": basePrice = 15 + random.nextDouble() * 60; break;
//            case "手机平板": basePrice = 200 + random.nextDouble() * 800; break;
//            default: basePrice = 20 + random.nextDouble() * 100; break;
//        }
//        return BigDecimal.valueOf(basePrice).setScale(2, RoundingMode.HALF_UP);
//    }
//
//    private BigDecimal generateDiscountRate() {
//        if (random.nextDouble() < 0.3) return BigDecimal.ONE;
//        double rate = 0.8 + random.nextDouble() * 0.15;
//        return BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
//    }
//
//    private Date randomDateInLast30Days() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(30));
//        cal.add(Calendar.HOUR_OF_DAY, -random.nextInt(24));
//        cal.add(Calendar.MINUTE, -random.nextInt(60));
//        return cal.getTime();
//    }
//
//    private String generateOrderNo() {
//        return "ORD" + System.currentTimeMillis() + String.format("%04d", random.nextInt(10000));
//    }
//
//    private Integer randomUserId(List<UsersEntity> users) {
//        if (users == null || users.size() < 2) throw new IllegalArgumentException("用户列表至少需要2个元素");
//        return users.get(1 + random.nextInt(users.size() - 1)).getUserId();
//    }
//
//    private Integer randomUserIdExcludeAdmin(List<UsersEntity> users) {
//        return randomUserId(users);
//    }
//
//    private Integer randomProductId(List<ProductsEntity> products) {
//        if (products == null || products.isEmpty()) throw new IllegalArgumentException("商品列表不能为空");
//        return products.get(random.nextInt(products.size())).getProductId();
//    }
//
//    private UsersEntity randomUser(List<UsersEntity> users) {
//        if (users == null || users.isEmpty()) throw new IllegalArgumentException("用户列表不能为空");
//        return users.get(random.nextInt(users.size()));
//    }
//
//    private ProductsEntity randomProduct(List<ProductsEntity> products) {
//        if (products == null || products.isEmpty()) throw new IllegalArgumentException("商品列表不能为空");
//        return products.get(random.nextInt(products.size()));
//    }
//
//    private String generateMessageContent() {
//        String[] contents = {"最低多少钱？诚心要", "可以便宜点吗？学生党", "明天能看货吗？在哪个宿舍？", "还能刀吗？可以的话现在下单", "已下单请尽快发货，急用", "支持验机吗？有无拆修？", "包邮吗？运费多少？", "有发票或购买记录吗？", "还在吗？想现在过来看", "能再拍几张实物图吗？"};
//        return contents[random.nextInt(contents.length)];
//    }
//
//    /**
//     * 数据统计类
//     */
//    private static class GenerationStats {
//        long clearTime, userTime, productTime, imageTime, cartTime, messageTime, orderTime, totalTime;
//        int userCount, productCount, imageCount, cartCount, messageCount, orderCount;
//
//        @Override
//        public String toString() {
//            return String.format(
//                    "总耗时: %d ms\n" +
//                            "├── 清空表: %d ms\n" +
//                            "├── 用户: %d 条 (%d ms)\n" +
//                            "├── 商品: %d 条 (%d ms)\n" +
//                            "├── 图片: %d 张 (%d ms)\n" +
//                            "├── 购物车: %d 条 (%d ms)\n" +
//                            "├── 消息: %d 条 (%d ms)\n" +
//                            "└── 订单: %d 条 (%d ms)",
//                    totalTime, clearTime, userCount, userTime, productCount, productTime,
//                    imageCount, imageTime, cartCount, cartTime, messageCount, messageTime,
//                    orderCount, orderTime
//            );
//        }
//    }
//}