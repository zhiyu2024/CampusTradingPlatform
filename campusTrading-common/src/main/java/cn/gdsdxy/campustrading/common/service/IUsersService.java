package cn.gdsdxy.campustrading.common.service;

import cn.gdsdxy.campustrading.common.model.dto.adminDto.AdminLoginParam;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.LoginParam;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.RegisterParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.UpdateUserParam;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.AdminInfoVo;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.DashboardVo;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.LoginVo;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.RegisterVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.UserInfoVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
public interface IUsersService extends IService<UsersEntity> {
    LoginVo login(LoginParam loginParam);
    RegisterVo registerUser(RegisterParam registerParam);
    UserInfoVo getUserInfo();  // 无参，内部自动获取
    void updateUserInfo(UpdateUserParam param);  // 无userId参数
    void userLogout(String token);
//    void deleteUser(String phone,String username,String password);//可以加个注销用户
//后面完善项目的时候再弄 时间不够了

//    AdminVo AdminLogin(AdminLoginParam param);
//   AdminInfoVo getAdminInfo();//String token
//    void AdminLogout();//String token
//    DashboardVo  getDashboardData();
}
