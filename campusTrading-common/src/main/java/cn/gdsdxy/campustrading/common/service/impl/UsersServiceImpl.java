package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.LoginParam;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.RegisterParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.UpdateUserParam;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.RegisterVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.UserInfoVo;
import cn.gdsdxy.campustrading.common.result.FwResultCode;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.LoginVo;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    @Value("${file.upload-images-path}") // ✅ 从配置读取路径
    private String uploadImagesPath;
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

}
