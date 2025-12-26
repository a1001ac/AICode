package com.ermao.aicode.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.exception.BusinessException;
import com.ermao.aicode.exception.ThrowUtils;
import com.ermao.aicode.mapper.UserMapper;
import com.ermao.aicode.model.dto.user.*;
import com.ermao.aicode.model.entity.User;
import com.ermao.aicode.model.enums.UserRoleEnum;
import com.ermao.aicode.model.vo.UserVO;
import com.ermao.aicode.satoken.DeviceUtils;
import com.ermao.aicode.service.UserService;
import com.ermao.aicode.utils.EmailUtil;
import com.ermao.aicode.utils.IpUtil;
import com.ermao.aicode.utils.RedisUtil;
import com.ermao.aicode.utils.email.ResetPasswordEmailProcessor;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ermao.aicode.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 21195
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private FileStorageService fileStorageService;

    private static final String RESET_PASSWORD_CODE_KEY_PREFIX = "reset_password_code:";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.检验参数
       ThrowUtils.throwIf(userAccount.length() < 3, ErrorCode.PARAMS_ERROR, "用户账号过短");
       ThrowUtils.throwIf(userPassword.length() < 6 || checkPassword.length() < 6, ErrorCode.PARAMS_ERROR, "用户密码过短");
       ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号重复");

        //3.密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        //4.插入数据到数据库中
        User user=new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserAvatar("https://ermao-1325310617.cos.ap-chengdu.myqcloud.com/AI/avatar/default-avatar.jpg");
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setUserGender(2);

        //5.获取IP并解析为地址
        String ip = IpUtil.getIp();
        String address = IpUtil.getIp2region(ip);
        user.setRegisterIp(ip);
        user.setRegisterAddress(address);

        boolean save = this.save(user);
        ThrowUtils.throwIf(!save,ErrorCode.SYSTEM_ERROR,"注册失败，数据库错误！");

        return user.getId();
    }

    /**
     * @param userPassword 原始密码
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "guochang";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, Boolean rememberMe, HttpServletRequest request) {
        // 1. 校验
        ThrowUtils.throwIf(StringUtils.isAllBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 3, ErrorCode.PARAMS_ERROR, "账号错误");
        ThrowUtils.throwIf(userPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码错误");

        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");

        // 更新用户登录信息
        String ip = IpUtil.getIp();
        String address = IpUtil.getIp2region(ip);
        user.setLoginIp(ip);
        user.setLoginAddress(address);
        this.updateById(user);

        // 3. 记录用户的登录态
        //request.getSession().setAttribute(USER_LOGIN_STATE, user);

        // 3. Sa-token 登录
        if (rememberMe) {
            // 勾选“记住我”：设置 Token 有效期（7天），且 Cookie 会持久化到硬盘
            StpUtil.login(user.getId(), new SaLoginParameter()
                    .setTimeout(60 * 60 * 24 * 7));
        } else {
            // 未勾选：Token 在浏览器关闭即消失
            StpUtil.login(user.getId(), new SaLoginParameter()
                    .setIsLastingCookie(false));
        }

        // 4. 把用户信息存入 SaSession
        StpUtil.getSession().set(USER_LOGIN_STATE, user);

        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser() {
        // 先判断是否已登录
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (User) StpUtil.getSession().get(USER_LOGIN_STATE);
    }


    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 移除登录态
        StpUtil.logout();
        return true;
    }

    @Override
    public boolean updateUser(UserUpdateRequest userUpdateRequest, User loginUser) {
        Long userId = userUpdateRequest.getId();
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR, "用户ID不能为空");

        User userToUpdate;
        // 检查是否是管理员
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());

        if (isAdmin) {
            // 管理员可以更新任何用户
            userToUpdate = this.getById(userId);
            ThrowUtils.throwIf(userToUpdate == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        } else {
            // 普通用户只能更新自己的信息
            ThrowUtils.throwIf(!userId.equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权修改他人信息");
            userToUpdate = loginUser;
        }

        if (StringUtils.isNotBlank(userUpdateRequest.getUserAccount())) {
            userToUpdate.setUserAccount(userUpdateRequest.getUserAccount());
        }
        if (ObjUtil.isNotNull(userUpdateRequest.getUserGender())) {
            userToUpdate.setUserGender(userUpdateRequest.getUserGender());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getUserAvatar())) {
            userToUpdate.setUserAvatar(userUpdateRequest.getUserAvatar());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getUserProfile())) {
            userToUpdate.setUserProfile(userUpdateRequest.getUserProfile());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getUserEmail())) {
            userToUpdate.setUserEmail(userUpdateRequest.getUserEmail());
        }

        boolean success = updateById(userToUpdate);

        // 如果更新的是当前登录用户，同步更新session中的用户信息
        if (userToUpdate.getId().equals(loginUser.getId())) {
            StpUtil.getSession().set(USER_LOGIN_STATE, userToUpdate);
        }

        return success;
    }

    @Override
    public String uploadUserAvatar(MultipartFile file) {
        Object userObj = StpUtil.getSession().get(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String originalFilename = file.getOriginalFilename();
        ThrowUtils.throwIf(originalFilename == null || !originalFilename.contains("."), ErrorCode.PARAMS_ERROR, "文件名错误");
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + fileSuffix;

        try {
            // 使用 x-file-storage 提供的 fluent API 上传文件
            FileInfo fileInfo = fileStorageService.of(file)
                    .setPath("avatar/")
                    .setSaveFilename(newFileName)
                    .upload();

            ThrowUtils.throwIf(fileInfo == null || fileInfo.getUrl() == null, ErrorCode.SYSTEM_ERROR, "文件上传失败");
            String fileAccessUrl = fileInfo.getUrl();

            // 更新用户头像地址并保存
            currentUser.setUserAvatar(fileAccessUrl);
            boolean ok = this.updateById(currentUser);
            ThrowUtils.throwIf(!ok, ErrorCode.OPERATION_ERROR, "更新用户头像失败");
            return fileAccessUrl;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }
    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null || userAddRequest.getUserAccount().length()< 3, ErrorCode.PARAMS_ERROR,"参数为空或账号名长度小于3");
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        final String DEFAULT_PASSWORD = "123456";
        String encryptPassword = getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserAccount(userAddRequest.getUserAccount());
        user.setUserPassword(encryptPassword);
        user.setUserGender(0);
        user.setUserAvatar("https://ermao-1325310617.cos.ap-chengdu.myqcloud.com/AI/avatar/default-avatar.jpg");
        user.setUserRole(userAddRequest.getUserRole());
        boolean save = this.save(user);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "新增用户失败");
        return user.getId();
    }

    @Override
    public boolean updateUserRole(UserUpdateRoleRequest userUpdateRoleRequest) {
        Long userId = userUpdateRoleRequest.getId();
        String userRole = userUpdateRoleRequest.getUserRole();

        // 1. 校验参数
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID错误");
        ThrowUtils.throwIf(StrUtil.isBlank(userRole), ErrorCode.PARAMS_ERROR, "用户角色不能为空");
        UserRoleEnum roleEnum = UserRoleEnum.getEnumByValue(userRole);
        ThrowUtils.throwIf(roleEnum == null, ErrorCode.PARAMS_ERROR, "角色设置错误");

        // 2. 校验用户是否存在
        User user = this.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 3. 更新角色
        user.setUserRole(userRole);
        boolean success = this.updateById(user);
        ThrowUtils.throwIf(!success, ErrorCode.SYSTEM_ERROR, "更新用户角色失败");

        return true;
    }

    @Override
    public boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {
        String oldPassword = userUpdatePasswordRequest.getOldPassword();
        String newPassword = userUpdatePasswordRequest.getNewPassword();
        String checkPassword = userUpdatePasswordRequest.getCheckPassword();

        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.isAllBlank(oldPassword, newPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
        User currentUser = this.getLoginUser();
        ThrowUtils.throwIf(!newPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        // 2. 校验旧密码
        String encryptedOldPassword = getEncryptPassword(oldPassword);
        ThrowUtils.throwIf(!encryptedOldPassword.equals(currentUser.getUserPassword()), ErrorCode.PARAMS_ERROR, "旧密码错误");

        // 3. 更新密码
        String encryptedNewPassword = getEncryptPassword(newPassword);
        currentUser.setUserPassword(encryptedNewPassword);
        boolean success = this.updateById(currentUser);
        ThrowUtils.throwIf(!success, ErrorCode.SYSTEM_ERROR, "密码修改失败");

        return true;
    }
    @Override
    public boolean sendResetPasswordCode(String email) {
        ThrowUtils.throwIf(StrUtil.isBlank(email), ErrorCode.PARAMS_ERROR, "邮箱不能为空");
        // 1. 校验邮箱是否存在
        User user = this.getOne(new QueryWrapper<User>().eq("userEmail", email));
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "该邮箱未注册");

        // 2. 生成6位验证码
        String code = String.valueOf(NumberUtil.generateRandomNumber(100000, 999999, 1)[0]);

        // 3. 将验证码存入Redis，有效期5分钟
        redisUtil.set(RESET_PASSWORD_CODE_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES);

        // 4. 发送邮件
        emailUtil.sendTemplateEmail(email, ResetPasswordEmailProcessor.class, code, "【AI零代码应用生成平台】重置密码");

        return true;
    }

    @Override
    public boolean retrievePassword(UserRetrievePasswordRequest userRetrievePasswordRequest) {
        String email = userRetrievePasswordRequest.getUserEmail();
        String code = userRetrievePasswordRequest.getCode().toString();
        String password = userRetrievePasswordRequest.getPassword();
        String checkPassword = userRetrievePasswordRequest.getCheckPassword();

        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.isAllBlank(email, code, password, checkPassword), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(!password.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        // 2. 校验验证码
        String redisKey = RESET_PASSWORD_CODE_KEY_PREFIX + email;
        Object storedCode = redisUtil.get(redisKey);
        ThrowUtils.throwIf(storedCode == null, ErrorCode.PARAMS_ERROR, "验证码已过期或不存在");
        ThrowUtils.throwIf(!code.equals(storedCode.toString()), ErrorCode.PARAMS_ERROR, "验证码错误");

        // 3. 校验用户是否存在
        User user = this.getOne(new QueryWrapper<User>().eq("userEmail", email));
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 4. 更新密码
        String encryptedPassword = getEncryptPassword(password);
        user.setUserPassword(encryptedPassword);
        boolean success = this.updateById(user);
        ThrowUtils.throwIf(!success, ErrorCode.SYSTEM_ERROR, "密码重置失败");

        // 5. 删除Redis中的验证码
        redisUtil.delete(redisKey);

        return true;
    }

    @Override
    public UserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO loginUserVO = new UserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    //分页查询接口
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        Integer userGender = userQueryRequest.getUserGender();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotNull(userGender), "userGender", userGender);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }
}
