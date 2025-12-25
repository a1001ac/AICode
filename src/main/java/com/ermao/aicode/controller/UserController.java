package com.ermao.aicode.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ermao.aicode.common.BaseResponse;
import com.ermao.aicode.common.DeleteRequest;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.common.Result;
import com.ermao.aicode.constant.UserConstant;
import com.ermao.aicode.exception.BusinessException;
import com.ermao.aicode.exception.ThrowUtils;
import com.ermao.aicode.model.dto.user.*;
import com.ermao.aicode.model.entity.User;
import com.ermao.aicode.model.vo.UserVO;
import com.ermao.aicode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileInfo; // 新增：FileInfo 类型
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.ermao.aicode.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private FileStorageService fileStorageService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return Result.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        UserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return Result.success(loginUserVO);
    }

    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser() {
        User user = userService.getLoginUser();
        //此时返回脱敏后的用户信息
        return Result.success(userService.getLoginUserVO(user));
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return Result.success(userService.userLogout(request));
    }

    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        return  Result.success(userService.addUser(userAddRequest));
    }

    @PostMapping("/updatePassword")
    public BaseResponse<Boolean> updateUserPassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        ThrowUtils.throwIf(userUpdatePasswordRequest == null, ErrorCode.PARAMS_ERROR);
        return Result.success(userService.updatePassword(userUpdatePasswordRequest));
    }

    @PostMapping("/sendResetCode")
    public BaseResponse<Boolean> sendResetPasswordCode(@RequestParam String email) {
        return Result.success(userService.sendResetPasswordCode(email));
    }

    @PostMapping("/retrievePassword")
    public BaseResponse<Boolean> userRetrievePassword(@RequestBody UserRetrievePasswordRequest userRetrievePasswordRequest) {
        ThrowUtils.throwIf(userRetrievePasswordRequest == null, ErrorCode.PARAMS_ERROR);
        return Result.success(userService.retrievePassword(userRetrievePasswordRequest));
    }

    @GetMapping("/get")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "请求的Id错误");
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "没有该用户Id");
        //此时返回脱敏后的用户信息
        return Result.success(user);
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        //此时返回脱敏后的用户信息
        return Result.success(userService.getUserVO(user));
    }

    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(deleteRequest == null || id <= 0, ErrorCode.PARAMS_ERROR, "参数传递错误");
        boolean success = userService.removeById(id);
        return Result.success(success);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser();
        boolean success = userService.updateUser(userUpdateRequest, loginUser);
        ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "更新用户失败");
        return Result.success(success);
    }

    @PostMapping("/update/role")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserRole(@RequestBody UserUpdateRoleRequest userUpdateRoleRequest) {
        ThrowUtils.throwIf(userUpdateRoleRequest == null, ErrorCode.PARAMS_ERROR);
        boolean success = userService.updateUserRole(userUpdateRoleRequest);
        return Result.success(success);
    }

    @PostMapping("/uploadAvatar")
    public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
        Object userObj = StpUtil.getSession().get(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String originalFilename = file.getOriginalFilename();
        ThrowUtils.throwIf(originalFilename == null || !originalFilename.contains("."), ErrorCode.PARAMS_ERROR, "文件名错误");
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileSuffix;

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
            boolean ok = userService.updateById(currentUser);
            ThrowUtils.throwIf(!ok, ErrorCode.OPERATION_ERROR, "更新用户头像失败");
            return Result.success(fileAccessUrl);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    @PostMapping("/list/page/vo")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return Result.success(userVOPage);
    }
}
