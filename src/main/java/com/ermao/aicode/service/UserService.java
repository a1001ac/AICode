package com.ermao.aicode.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ermao.aicode.model.dto.user.*;
import com.ermao.aicode.model.entity.User;
import com.ermao.aicode.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 21195
*/
public interface UserService extends IService<User> {
    /**
     *用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 获取加密后的密码
     *
     * @param userPassword 原始密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      HttpServletRequest
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    User getLoginUser();

    /**
     * 获取脱敏的已登录用户信息
     * @return 脱敏的已登录用户信息
     */
    UserVO getLoginUserVO(User user);

    /**
     * 用户退出登录
     *
     * @param request request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateUser(UserUpdateRequest userUpdateRequest, User loginUser);

    /**
     * 管理员添加用户
     * @param userAddRequest
     * @return
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 管理员修改用户角色
     * @param userUpdateRoleRequest
     * @return
     */
    boolean updateUserRole(UserUpdateRoleRequest userUpdateRoleRequest);

    /**
     * 更改密码
     */
    boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest);

    /**
     * 发送重置密码验证码
     * @param email 邮箱
     */
    boolean sendResetPasswordCode(String email);

    /**
     * 找回密码
     * @param userRetrievePasswordRequest
     * @return
     */
    boolean retrievePassword(UserRetrievePasswordRequest userRetrievePasswordRequest);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    //获取mp查询所需的方法
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
