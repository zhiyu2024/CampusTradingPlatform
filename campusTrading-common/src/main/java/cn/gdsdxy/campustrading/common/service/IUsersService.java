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

    UserInfoVo getUserInfo();  // 无参，内部自动获取
    void updateUserInfo(UpdateUserParam param);  // 无userId参数
    void logout(String token);

    LoginVo login(LoginParam loginParam);
    RegisterVo registerUser(RegisterParam registerParam);



    /**
     * 管理员登录
     */
    FwResult login(AdminLoginParam param);

    /**
     * 获取管理员信息
     */
    FwResult<AdminInfoVo> getAdminInfo();//String token

    /**
     * 管理员退出登录
     */
    FwResult logout();//String token

    /**
     * 获取数据概览
     */
    FwResult<DashboardVo>  getDashboardData();
}
