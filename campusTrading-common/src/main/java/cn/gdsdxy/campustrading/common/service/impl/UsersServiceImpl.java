package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.controller.vo.LoginVo;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Autowired
UsersMapper usersMapper;
     @Override
    public String UserLogin(LoginVo loginVo){

         LambdaQueryWrapper<UsersEntity> wrapper=new LambdaQueryWrapper<>();
          wrapper.eq(UsersEntity::getUsername,loginVo.getUsername());
         UsersEntity user= usersMapper.selectOne(wrapper);
         if(user==null){
             return "用户名不存在";
         }else{
             String password=user.getPassword();
             // 1. 初始化 Spring Security 推荐的加密器
             BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
             boolean isMatch = encoder.matches(loginVo.getPassword(), password);
             String mes;
             if(isMatch){
                 mes="登录成功";
             }else{
                 mes="密码错误,请重新输入";
             }
             return mes;
         }

     }

}
