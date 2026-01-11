package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.CategoriesEntity;
import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.CategoriesMapper;
import cn.gdsdxy.campustrading.common.mapper.OrdersMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.model.dto.adminDto.AdminLoginParam;
import cn.gdsdxy.campustrading.common.model.dto.adminDto.AdminRegisterParam;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.LoginParam;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.RegisterParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.UpdateUserParam;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.*;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.RegisterVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.UserInfoVo;
import cn.gdsdxy.campustrading.common.result.FwResultCode;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.LoginVo;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, UsersEntity> implements IUsersService {
    // 1. 注入 RedisTemplate
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入加密器
    @Value("${file.upload-images-path}") // ✅ 从配置读取路径
    private String uploadImagesPath;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    UsersMapper usersMapper;
    @Autowired
    private ProductsMapper productsMapper;
    @Autowired
    private CategoriesMapper productCategoryMapper; // 商品分类表Mapper
    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public LoginVo login(LoginParam loginParam) {
        // 1. 查询用户
        LambdaQueryWrapper<UsersEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsersEntity::getUsername, loginParam.getUsername());
        UsersEntity user = usersMapper.selectOne(wrapper);
        //用户不存在或者密码错误
        if (user == null || !passwordEncoder.matches(loginParam.getPassword(), user.getPassword())) {
            // 抛出自定义业务异常，传入之前写的枚举或错误信息
            throw new BusinessException(FwResultCode.USER_LOGIN_ERROR);
        }

        // 3. 生成 Token
        String token = jwtUtils.createToken(user.getUserId(), user.getUsername());
        // --- 2. 存入 Redis ---
        // 建议 Key 的格式：项目名:模块名:唯一标识
        // 例如：campus:login:token:xxxxxx
        String redisKey = "campus:login:token:" + token;

        //  为了安全和性能，我们把 user 对象（或者是 VO）存进去,设置过期时间，应该与 JWT 的过期时间保持一致（比如 24 小时）
        redisTemplate.opsForValue().set(redisKey, user, 24, TimeUnit.HOURS);

        LoginVo loginVO = BeanUtil.copyProperties(user, LoginVo.class);
        loginVO.setToken(token);
        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterVo registerUser(RegisterParam param) {
        try {
            // 1. 校验
            validateUsername(param.getUsername());
            if (StringUtils.isNotBlank(param.getPhone())) {
                validatePhone(param.getPhone());
            }

            // 2. 上传头像
            MultipartFile file = param.getAvatar();
            String avter = saveFile(file);
            String avatarUrl = "/res/images/" + avter;
//        String avatarUrl = uploadAvatar(param.getAvatar());

            // 3. 保存用户
            UsersEntity user = BeanUtil.copyProperties(param, UsersEntity.class);
            user.setPassword(passwordEncoder.encode(param.getPassword()));
            user.setAvatar(avatarUrl);
            user.setRole((byte) 0);
            user.setStatus((byte)1);
            usersMapper.insert(user);
            RegisterVo registerVo=BeanUtil.copyProperties(user, RegisterVo.class);
            return registerVo ;
        }catch (IOException e){
            log.error("头像上传失败: {}", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }

    }

    @Override
    public UserInfoVo getUserInfo() {
        Long userId = SecurityUtil.getUserId();
        UsersEntity user = usersMapper.selectById(userId.intValue());
        if (user == null) throw new BusinessException(905,"用户不存在");
        UserInfoVo userInfoVo=BeanUtil.copyProperties(user, UserInfoVo.class);
        return userInfoVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UpdateUserParam param) {
        Long userId = SecurityUtil.getUserId();
        UsersEntity user = usersMapper.selectById(userId.intValue());
        if (user == null) throw new BusinessException(905,"用户不存在");

        // 校验手机号是否被占用
        if (StringUtils.isNotBlank(param.getPhone()) && !param.getPhone().equals(user.getPhone())) {
            validatePhone(param.getPhone());
        }
        // 处理头像
        String avatarUrl = user.getAvatar();

        if (param.getAvatar() != null && !param.getAvatar().isEmpty()) {
            try{
//            删除旧头像
                deleteOldAvatar(avatarUrl);
                // 2. 上传新头像
                MultipartFile file = param.getAvatar();
                String avter = saveFile(file);
                String newAvatarUrl = "/res/images/" + avter;
//            更新头像数据
                avatarUrl = newAvatarUrl;
            }catch(IOException e){
                log.error("头像上传失败: {}", e);
                throw new RuntimeException("头像上传失败: " + e.getMessage(), e);
            }

        }
        // 更新（只更新非空字段）
        UsersEntity update = new UsersEntity();
        update.setNickname(param.getNickname());
        update.setPhone(param.getPhone());
        update.setAvatar(avatarUrl);
        update.setCampus(param.getCampus());
//        LocalDateTime now = LocalDateTime.now();
//        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        usersMapper.updateById(update);
    }

    @Override
    public void userLogout(String token) {
        if (StringUtils.isNotBlank(token)) {
            redisTemplate.delete("campus:login:token:" + token);
        }
    }

    // ===== 私有方法 =====

    private String saveFile(MultipartFile file ) throws IOException {
            // 校验
            if (!file.getContentType().startsWith("image/")) {
                throw new BusinessException(1005, "必须是图片");
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new BusinessException(1006, "不能超过5MB");
            }

            // 生成文件名
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = String.format("%s_%d%s", "img",
                    System.currentTimeMillis(), suffix);

            // 保存
            // 3. 确保目录存在
            File dir = new File(uploadImagesPath); //看上传的文件的路径里的图片是否存在 全局文件配置
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 4. 保存文件
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);

            return fileName; // 返回新文件名  不包括路劲
    }

    private void deleteOldAvatar(String avatarUrl)  {
        if (StringUtils.isNotBlank(avatarUrl)) {
            try {
                String fileName = avatarUrl;
                File file = new File(uploadImagesPath + fileName);
                if (file.exists()) file.delete();
            } catch (Exception ignored) {}
        }
    }

    private void validateUsername(String username) {
        if (lambdaQuery().eq(UsersEntity::getUsername, username).count() > 0) {
            throw new BusinessException(903, "用户名已存在");
        }
    }

    private void validatePhone(String phone) {
        if (lambdaQuery().eq(UsersEntity::getPhone, phone).count() > 0) {
            throw new BusinessException(904, "手机号已被注册");
        }
    }


//  ===============================  ------------------管理员-----------------==============================================================

    // ========== 管理员登录 ==========
    @Override
    public AdminInfoVo AdminLogin(AdminLoginParam param) {
        // 1. 查：用户名+角色=1（管理员）+状态正常的用户
        UsersEntity admin = usersMapper.selectOne(new LambdaQueryWrapper<UsersEntity>()
                .eq(UsersEntity::getUsername, param.getUsername())
                .eq(UsersEntity::getRole, 1)
                .eq(UsersEntity::getStatus, 1)
        );

        // 2. 校验：用户不存在/密码错误
        if (admin == null || !passwordEncoder.matches(param.getPassword(), admin.getPassword())) {
            throw new BusinessException(5001,"用户名或密码错误");
        }

        // 3. 生成Token+存Redis（过期2小时）
        String token = jwtUtils.createToken(admin.getUserId(), admin.getUsername());
        String redisKey = "admin:token:" + admin.getUserId();
        redisTemplate.opsForValue().set(redisKey, token, 2, TimeUnit.HOURS);

        // 4. 组装VO返回
        AdminInfoVo vo = new AdminInfoVo();
        BeanUtils.copyProperties(admin, vo);
        vo.setToken(token);
        return vo;
    }

    // ========== 获取当前管理员信息 ==========
    @Override
    public AdminInfoVo getAdminInfo() {
        // 从上下文自动获取当前登录的管理员ID（无需前端传参）
        Long adminId = SecurityUtil.getUserId();

        // 查管理员信息
        UsersEntity admin = usersMapper.selectById(adminId);
        if (admin == null || admin.getRole() != 1) {
            throw new BusinessException(4007,"非法访问");
        }

        // 组装VO
        AdminInfoVo vo = new AdminInfoVo();
        BeanUtils.copyProperties(admin, vo);
        return vo;
    }

    // ========== 管理员退出登录 ==========
    @Override
    public void AdminLogout() {
        // 获取当前管理员ID
        Long adminId = SecurityUtil.getUserId();
        // 删除Redis中的Token
        redisTemplate.delete("admin:token:" + adminId);
    }

    // ========== 管理员注册（可选，仅超级管理员可用） ==========
    @Override
    public void Adminregister(AdminRegisterParam param) {
        // 校验用户名是否重复
        if (usersMapper.selectOne(new LambdaQueryWrapper<UsersEntity>()
                .eq(UsersEntity::getUsername, param.getUsername())) != null) {
            throw new BusinessException(4009,"用户名已存在");
        }

        // 加密密码+插入管理员
        UsersEntity admin = new UsersEntity();
        admin.setUsername(param.getUsername());
        admin.setPassword(passwordEncoder.encode(param.getPassword()));
        admin.setNickname(param.getNickname());
        admin.setRole((byte)1); // 标记为管理员
        admin.setStatus((byte)1); // 状态正常
        admin.setCreatedAt(new Date());
        usersMapper.insert(admin);
    }

    @Override
    public  DashboardVo getDashboardData(){
        DashboardVo dashboardVo = new DashboardVo();

        // ========== 1. 核心指标：用户/商品/订单/总交易额 ==========
        // 1.1 用户总数
        Long totalUsers = usersMapper.selectCount(null);
        dashboardVo.setTotalUsers(totalUsers);

        // 1.2 商品总数
        Long totalProducts = productsMapper.selectCount(null);
        dashboardVo.setTotalProducts(totalProducts);

        // 1.3 订单总数
        Long totalOrders = ordersMapper.selectCount(null);
        dashboardVo.setTotalOrders(totalOrders);

        // 1.4 总交易额（仅统计“已完成”订单，对应status=1）
        List<Object> amountList = ordersMapper.selectObjs(
                new LambdaQueryWrapper<OrdersEntity>()
                        .select(OrdersEntity::getTotalAmount)
                        .eq(OrdersEntity::getStatus, 1) // 你的订单表status=1是“已完成”
        );
        BigDecimal totalTransactionAmount = amountList.stream()
                .map(obj -> (BigDecimal) obj)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboardVo.setTotalTransactionAmount(totalTransactionAmount);


        // ========== 2. 商品分类销售占比（基于orders表关联商品+分类） ==========
        List<Map<String, Object>> categorySalesStats = ordersMapper.selectCategorySalesStats();
        dashboardVo.setCategorySalesStats(categorySalesStats);


        // ========== 3. 最近7天趋势数据（基于orders表统计） ==========
        List<Map<String, Object>> weeklyTrend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate currentDate = today.minusDays(i);
            Date startDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(currentDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 3.1 当日新增用户
            Long newUsers = usersMapper.selectCount(
                    new LambdaQueryWrapper<UsersEntity>()
                            .ge(UsersEntity::getCreatedAt, startDate)
                            .lt(UsersEntity::getCreatedAt, endDate)
            );

            // 3.2 当日新增商品
            Long newProducts = productsMapper.selectCount(
                    new LambdaQueryWrapper<ProductsEntity>()
                            .ge(ProductsEntity::getCreatedAt, startDate)
                            .lt(ProductsEntity::getCreatedAt, endDate)
            );

            // 3.3 当日新增订单
            Long newOrders = ordersMapper.selectCount(
                    new LambdaQueryWrapper<OrdersEntity>()
                            .ge(OrdersEntity::getCreatedAt, startDate)
                            .lt(OrdersEntity::getCreatedAt, endDate)
            );

            // 3.4 当日交易额（仅统计已完成订单）
            List<Object> dayAmountList = ordersMapper.selectObjs(
                    new LambdaQueryWrapper<OrdersEntity>()
                            .select(OrdersEntity::getTotalAmount)
                            .ge(OrdersEntity::getCreatedAt, startDate)
                            .lt(OrdersEntity::getCreatedAt, endDate)
                            .eq(OrdersEntity::getStatus, 1)
            );
            BigDecimal daySales = dayAmountList.stream()
                    .map(obj -> (BigDecimal) obj)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 组装当日数据
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", currentDate.format(DateTimeFormatter.ofPattern("MM-dd")));
            dayData.put("newUsers", newUsers);
            dayData.put("newProducts", newProducts);
            dayData.put("newOrders", newOrders);
            dayData.put("sales", daySales);
            weeklyTrend.add(dayData);
        }
        dashboardVo.setWeeklyTrend(weeklyTrend);


        return dashboardVo;
    }



    @Override
    public DataBoardVo getDataBoardData() {
        DataBoardVo dataBoardVo = new DataBoardVo();

        // 1. 组装【用户增长趋势】数据（近12个月，按月份统计用户数）
        dataBoardVo.setUserGrowthList(this.getUserGrowthData());

        // 2. 组装【商品分类统计】数据（饼图：分类名+商品数）
        dataBoardVo.setCategoryStatList(this.getCategoryStatData());

        // 3. 组装【订单状态分布】数据（环形图：状态名+订单数）
        dataBoardVo.setOrderStatusStatList(this.getOrderStatusStatData());

        // 4. 组装【销售额趋势】数据（近12个月，按月份统计销售额）
        dataBoardVo.setSalesTrendList(this.getSalesTrendData());

        return dataBoardVo;
    }

    // ========== 私有方法：查询各模块数据 ==========
    /**
     * 查询用户增长趋势数据（近12个月）
     */
    private List<UserGrowthVo> getUserGrowthData() {
        List<UserGrowthVo> list = new ArrayList<>();
        // 示例：MyBatis-Plus查询（实际可通过SQL分组更高效，这里简化）
        // 步骤1：查询所有用户的创建时间，按月份分组统计
        List<UsersEntity> users = usersMapper.selectList(null);
        if (CollectionUtils.isEmpty(users)) {
            return list;
        }

        // 按月份分组（简化：取createdAt的月份，格式如"1月"）
        Map<String, Long> monthUserMap = users.stream()
                .collect(Collectors.groupingBy(
                        user -> {
                            // 提取月份（实际项目用SimpleDateFormat格式化，这里简化）
                            return (user.getCreatedAt().getMonth() + 1) + "月";
                        },
                        Collectors.counting()
                ));

        // 转换为UserGrowthVo列表
        for (Map.Entry<String, Long> entry : monthUserMap.entrySet()) {
            UserGrowthVo vo = new UserGrowthVo();
            vo.setMonth(entry.getKey());
            vo.setUserCount(entry.getValue());
            list.add(vo);
        }
        return list;
    }

    /**
     * 查询商品分类统计数据
     */
    private List<CategoryStatVo> getCategoryStatData() {
        List<CategoryStatVo> list = new ArrayList<>();
        // 步骤1：查询所有商品分类
        List<CategoriesEntity> categories = productCategoryMapper.selectList(null);
        if (CollectionUtils.isEmpty(categories)) {
            return list;
        }

        // 步骤2：查询所有商品，按分类ID分组统计数量
        List<ProductsEntity> products = productsMapper.selectList(null);
        Map<Integer, Long> categoryProductMap = products.stream()
                .collect(Collectors.groupingBy(
                        ProductsEntity::getCategoryId,
                        Collectors.counting()
                ));

        // 步骤3：组装分类名+商品数
        for (CategoriesEntity category : categories) {
            CategoryStatVo vo = new CategoryStatVo();
            vo.setCategoryName(category.getCategoryName());
            vo.setProductCount(categoryProductMap.getOrDefault(category.getCategoryId(), 0L));
            list.add(vo);
        }
        return list;
    }

    /**
     * 查询订单状态分布数据
     */
    private List<OrderStatusStatVo> getOrderStatusStatData() {
        List<OrderStatusStatVo> list = new ArrayList<>();
        // 步骤1：查询所有订单，按状态分组统计数量
        List<OrdersEntity> orders = ordersMapper.selectList(null);
        if (CollectionUtils.isEmpty(orders)) {
            return list;
        }

        // 按状态分组，转换状态码为状态名（结合你之前的OrderStatusEnum）
        Map<Byte, Long> statusOrderMap = orders.stream()
                .collect(Collectors.groupingBy(
                        OrdersEntity::getStatus,
                        Collectors.counting()
                ));

        // 步骤2：组装状态名+订单数
        for (Map.Entry<Byte, Long> entry : statusOrderMap.entrySet()) {
            OrderStatusStatVo vo = new OrderStatusStatVo();
            // 状态码转名称（0=待付款，1=已付款，2=已发货，3=已完成，4=已取消）
            switch (entry.getKey()) {
                case 0: vo.setStatusName("待付款"); break;
                case 1: vo.setStatusName("已付款"); break;
                case 2: vo.setStatusName("已发货"); break;
                case 3: vo.setStatusName("已完成"); break;
                case 4: vo.setStatusName("已取消"); break;
                default: vo.setStatusName("未知状态");
            }
            vo.setOrderCount(entry.getValue());
            list.add(vo);
        }
        return list;
    }

    /**
     * 查询销售额趋势数据（近12个月）
     */
    private List<SalesTrendVo> getSalesTrendData() {
        List<SalesTrendVo> list = new ArrayList<>();
        // 步骤1：查询所有订单（仅统计已付款/已完成的订单）
        LambdaQueryWrapper<OrdersEntity> wrapper = new LambdaQueryWrapper<OrdersEntity>()
                .in(OrdersEntity::getStatus, 1, 3); // 1=已付款，3=已完成
        List<OrdersEntity> orders = ordersMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(orders)) {
            return list;
        }

        // 步骤2：按月份分组统计销售额
        Map<String, BigDecimal> monthSalesMap = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> (order.getCreatedAt().getMonth() + 1) + "月", // 简化取月份
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                OrdersEntity::getTotalAmount,
                                BigDecimal::add
                        )
                ));

        // 步骤3：转换为SalesTrendVo列表
        for (Map.Entry<String, BigDecimal> entry : monthSalesMap.entrySet()) {
            SalesTrendVo vo = new SalesTrendVo();
            vo.setMonth(entry.getKey());
            vo.setSalesAmount(entry.getValue());
            list.add(vo);
        }
        return list;
    }












}
