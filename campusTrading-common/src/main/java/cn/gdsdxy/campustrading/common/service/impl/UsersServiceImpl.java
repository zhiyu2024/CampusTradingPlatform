package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.LoginParam;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.RegisterParam;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.RegisterVo;
import cn.gdsdxy.campustrading.common.result.FwResultCode;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.LoginVo;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
    @Value("${upload.path.images:/app/upload/images/}") private String uploadPath;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    UsersMapper usersMapper;

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
    public RegisterVo registerUser(RegisterParam registerParam) {

        // 1. 校验用户名是否已存在
        LambdaQueryWrapper<UsersEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsersEntity::getUsername, registerParam.getUsername());
        if (usersMapper.selectOne(wrapper) != null) {
            throw new BusinessException(1003, "用户名已存在");  // 添加错误码 抛出异常,终止以下操作
            //抛出后：当前方法会立即停止执行，异常会向上层传播 抛出 throw  抛出 新 异常 错误码是:...
        }
        //用户名不存在就执行下面的:
        // 2. 校验手机号是否已存在（如果提供了手机号）
        if (StringUtils.isNotBlank(registerParam.getPhone())) {
            LambdaQueryWrapper<UsersEntity> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(UsersEntity::getPhone, registerParam.getPhone());
            UsersEntity phone = usersMapper.selectOne(wrapper1);
            if (phone != null) {
                throw new BusinessException(1004, "手机号已被注册");
            }
        }
        // 3. 注册用户 添加新用户
        UsersEntity user = new UsersEntity();
        user.setStudentNo(registerParam.getStudentNo());
        user.setUsername(registerParam.getUsername());
        String password = passwordEncoder.encode(registerParam.getPassword());
        user.setPassword(password);
        user.setNickname(registerParam.getNickname());
        user.setPhone(registerParam.getPhone());
        user.setAvatar(registerParam.getAvatar());
        user.setCampus(registerParam.getCampus());

        usersMapper.insert(user);
//        this.save(user);
        RegisterVo registerVo = BeanUtil.copyProperties(user, RegisterVo.class);
        return registerVo;

    }
}
